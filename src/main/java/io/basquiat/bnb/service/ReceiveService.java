package io.basquiat.bnb.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import io.basquiat.bnb.domain.LastBlockHeight;
import io.basquiat.bnb.domain.LastBlockHeightStore;
import io.basquiat.bnb.domain.entity.BlockTransactions;
import io.basquiat.bnb.repository.BlockTransactionsRepository;
import io.basquiat.common.code.CategoryCode;
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

	@Autowired
	private Wallet wallet;
	
	@Autowired
	@Qualifier("nodeClient")
	private BinanceDexApiNodeClient nodeClient;
	
	@Autowired
	@Qualifier("restClient")
	private BinanceDexApiRestClient restClient;
	
	@Autowired
	private BlockTransactionsRepository blockTransactionsRepo;
	
	/**
	 * bnbDepositCheck from binance node
	 * @return Mono<Void>
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
