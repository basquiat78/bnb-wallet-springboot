package io.basquiat.bnb.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.binance.dex.api.client.BinanceDexApiNodeClient;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.BlockMeta;
import com.binance.dex.api.client.domain.Transaction;
import com.binance.dex.api.client.domain.TransactionPage;
import com.binance.dex.api.client.domain.TransactionType;
import com.binance.dex.api.client.domain.request.TransactionsRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.basquiat.bnb.domain.LastBlockHeight;
import io.basquiat.bnb.domain.LastBlockHeightStore;
import io.basquiat.bnb.domain.entity.BlockTransactions;
import io.basquiat.bnb.repository.BlockTransactionsRepository;
import io.basquiat.common.code.CategoryCode;
import io.basquiat.config.FindAndSaveEvent;
import io.basquiat.utils.BnbDateUtils;
import io.basquiat.utils.CommonUtils;
import io.basquiat.utils.FileIOUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * binance coin service
 * 
 * created by basquiat
 *
 */
@Slf4j
@Service("receiveService")
public class ReceiveService {

	@Value("${bnb.prefix}")
	private String BNB_PREFIX;
	
	@Autowired
	private Wallet wallet;
	
	@Autowired
	@Qualifier("nodeClient")
	private BinanceDexApiNodeClient nodeClient;
	
	@Autowired
	@Qualifier("restClient")
	private BinanceDexApiRestClient restClient;

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private BlockTransactionsRepository blockTransactionsRepo;
	
	/**
	 * 웹소켓으로부터 메세지를 받으면 호출 되는 메소드
	 * @param message
	 */
	public void process(String message) {
		log.info("Recieved Message : {}", message);
		// gson으로 jsonObject 객체로 변환한다.
		//{"stream":"transfers","data":{"e":"outboundTransferInfo","E":30033127,"H":"574EEB75A417044E46C79D06A63F437127FF038B8082ABC699B9773EE8F8E3F6","f":"tbnb14l4ptym53gx2sr86372l8x5l4089ns3a3z4evx","t":[{"o":"tbnb17rg2daudfezctq7c40rl04p0jktkglqczsjy6l","c":[{"a":"BNB","A":"0.01000000"}]}]}}
		JsonObject jsonObject = CommonUtils.convertObjectFromStringUsingGson(message);
		String stream = jsonObject.get(CategoryCode.STREAM.code).getAsString();
		//소켓으로 받은 메세지로부터 파싱한 json에서 stream키가 transfers인 넘만 체크한다.
		if(stream.equals(CategoryCode.TRANSFERS.code)) {
			// 입금 주소로 넘어온 주소가 같은지 체크해야한다.
			JsonObject data = jsonObject.get(CategoryCode.DATA.code).getAsJsonObject();
			JsonArray ja = data.get(CategoryCode.T.code).getAsJsonArray();
			String toAddress = ja.get(0).getAsJsonObject().get(CategoryCode.O.code).getAsString();
			if(wallet.getAddress().equals(toAddress)) {
				String txHash = data.get(CategoryCode.H.code).getAsString();
				// 메세지를 받으면 이벤트를 txHahs를 실어서 publish한다.
				context.publishEvent(new FindAndSaveEvent(txHash));
			}
		}
	}
	
	/**
	 * bnbDepositCheck from binance node
	 * @return void
	 */
	public void schedulingBnbDepositCheck() {
		log.info("deposit checking scheduler start");
		String depositAddr = wallet.getAddress();
		
		long lastBlockHeight = FileIOUtils.readFile().getLastBlockHeight();
		this.setLastBlockHeightStore(lastBlockHeight, false);
		
		// 최초 처음 시작할때 주소로 transaction 리스트를 가져온다.
		// 이 정보는 최근 24시간동안 발생한 트랜잭션 정보를 가져오게 된다.
		// 따라서 처음 읽어와서  
		TransactionsRequest request = new TransactionsRequest();
		request.setAddress(depositAddr);
		request.setTxType(TransactionType.TRANSFER);
		TransactionPage transactionPage = restClient.getTransactions(request);
		// 이중에 필요한 정보는 toAddress가 입금 주소이고 txType이 TRANSFER인 녀석들만 수집한다.
		// 내림차순으로 정렬. 이유는 마지막 블록은 최종적으로 검사한 마지막 블록이 될테니 이 블록은 파일에 저장할 예정이다.
		// 이유는 한번 조회한 트랜잭션은 제외하기 위해서이다.
		List<Transaction> transactionList = transactionPage.getTx().stream()
																   .filter(tr -> CategoryCode.TRANSFER.name().equals(tr.getTxType()) &&
																		   		 depositAddr.equals(tr.getToAddr()) && 
																		   		 tr.getBlockHeight() > lastBlockHeight )
																   .sorted(Comparator.comparing(Transaction::getBlockHeight))
																   .collect(Collectors.toList());
		
		// 루프를 돌면서 트랜잭션의 정보를 읽고 들어온 deposit 정보를 인서트한다.
		for(Transaction transaction : transactionList) {
			log.info("transaction --> : " + CommonUtils.convertJsonStringFromObject(transaction));
			this.searchDepositInfo(transaction);
		}
		log.info("deposit checking scheduler end");
	}
	
	@Async
	private void searchDepositInfo(Transaction tr) {
		String txHash  = tr.getTxHash();
		String timeStamp = tr.getTimeStamp();
		String amount = tr.getValue();
		long blockHeight = tr.getBlockHeight();
		// nodeClient로 가져온 tx와 restClient로 가져온 tx의 도메인이 다르다.
		// 메모 정보는 여기서 가져와야 한다.......그지같넹..
		try {
			com.binance.dex.api.client.domain.broadcast.Transaction tx = nodeClient.getTransaction(txHash);
			String memo = tx.getMemo();
			BlockMeta blockMeta = nodeClient.getBlockMetaByHeight(tx.getHeight());
			String blockHash = blockMeta.getBlockId().getHash();
			
			// 이 정보를 토대로 BlockTransactions 객체를 생성한다.
			BlockTransactions blockTransactions = BlockTransactions.builder()
																   .txHash(txHash)
																   .blockHeight(blockHeight)
																   .blockHash(blockHash)
																   .action(CategoryCode.RECEIVE.code)
																   .txType(CategoryCode.TRANSFER.code)
																   .toAddress(tr.getToAddr())
																   .fromAddress(tr.getFromAddr())
																   .value(amount)
																   .txAge(tr.getTxAge())
																   .txAsset(tr.getTxAsset())
																   .txFee(tr.getTxFee())
																   .memo(memo)
																   .timeStamp(BnbDateUtils.convertDateFromBNBTimeStamp(timeStamp))
																   .build();
			try {
				blockTransactionsRepo.save(blockTransactions);
				log.info("Binance Coin Block Transactions DB Insert Success : " + CommonUtils.convertJsonStringFromObject(blockTransactions));
			} catch(Exception e) {
				log.info("Binance Coin Block Transactions DB Insert Fail : Check Date --> " + CommonUtils.convertJsonStringFromObject(blockTransactions));
			}
			
		} catch (Exception e) {
			log.info("error message : " + e.getMessage());
		}
		this.setLastBlockHeightStore(blockHeight, true);
	}
	
	/**
	 * 블록 정보를 메모리에 올린다.
	 * @param blockHeight
	 */
	private void setLastBlockHeightStore(long blockHeight, boolean isWriteFile) {
		LastBlockHeight lbh = LastBlockHeight.builder().lastBlockHeight(blockHeight).build();
		LastBlockHeightStore.setLastBlockHeigh(lbh);
		if(isWriteFile) {
			FileIOUtils.writeFile(lbh);
		}
	}
	
}
