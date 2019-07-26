package io.basquiat.bnb.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.binance.dex.api.client.Wallet;
import com.google.gson.Gson;

import io.basquiat.bnb.domain.entity.BlockTransactions;
import io.basquiat.bnb.domain.response.Transaction;
import io.basquiat.bnb.domain.response.TransactionsResponse;
import io.basquiat.bnb.repository.BlockTransactionsRepository;
import io.basquiat.common.code.BinanceApiUri;
import io.basquiat.common.code.CategoryCode;
import io.basquiat.common.code.EnvironmentCode;
import io.basquiat.utils.BnbDateUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 입금받은거 처리하는 서비스
 * 
 * created by basquiat
 *
 */
@Slf4j
@Service("testService")
public class DepositService {

	@Value("${bnb.prefix}")
	private String BNB_PREFIX;
	
	@Autowired
	private Wallet wallet;
	
	@Autowired
	private Gson gson;
	
	@Autowired
	private BlockTransactionsRepository blockTransactionsRepo;
	
	/**
	 * API를 호출해서 transaction list를 받아온다. <-- 호출하면 받는 transaction list는 최근 24시간 발생한 트랜잭션 정보를 가져오게 된다.
	 * 넘겨받은 txHash로 필터링을 걸어 원하는 정보를 수집해 DB에 인서트한다.
	 * @param txHash
	 * @throws Exception 
	 */
	@Async
	public void findAndSave(String txHash) {
		log.info("find And Save Start");
		String binanceRestApiURL = EnvironmentCode.fromString(BNB_PREFIX).getApiURL(BNB_PREFIX);
		String apiURL = binanceRestApiURL + BinanceApiUri.TRANSACTIONS.URI + wallet.getAddress();
		try {
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod(CategoryCode.GET.code);
			int responseCode = con.getResponseCode();
			
			BufferedReader br;
			if(responseCode==200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {  // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			// 잭슨이를 쓰면 에러난다....그래서 gson으로 변경....
			// 잭슨이 아저씨 배반인데...
			System.out.println(response.toString());
			System.out.println("txHash ----> " + txHash);
			TransactionsResponse transactionResponse = gson.fromJson(response.toString(), TransactionsResponse.class);
			Transaction result = transactionResponse.getTx().stream()
														  	.filter(tr -> txHash.equals(tr.getTxHash()))
														  	.map(tr -> tr)
														  	.findFirst()
														  	.orElse(null);
			
			BlockTransactions bt = this.convertToBlockTransactions(result);
			blockTransactionsRepo.save(bt);
			
		} catch (Exception e) {
			log.info("Error Message : {}", e.getMessage());
		}
	}

	/**
	 * 번거롭지만 디비에 저장한 엔티티를 생성해 준다.
	 * @param transaction
	 * @return BlockTransactions
	 */
	private BlockTransactions convertToBlockTransactions(Transaction transaction) {
		return BlockTransactions.builder()
							    .txHash(transaction.getTxHash())
							    .blockHeight(transaction.getBlockHeight())
							    .action(CategoryCode.RECEIVE.code)
							    .txType(transaction.getTxType())
							    .toAddress(transaction.getToAddr())
							    .fromAddress(transaction.getFromAddr())
							    .value(transaction.getValue())
							    .txAge(transaction.getTxAge())
							    .txAsset(transaction.getTxAsset())
							    .txFee(transaction.getTxFee())
							    .memo(transaction.getMemo())
							    .timeStamp(BnbDateUtils.convertDateFromBNBTimeStamp(transaction.getTimeStamp()))
							    .build();
	}
	
}
