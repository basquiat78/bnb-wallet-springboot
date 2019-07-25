package io.basquiat.bnb.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.Account;
import com.binance.dex.api.client.domain.TransactionMetadata;
import com.binance.dex.api.client.domain.broadcast.TransactionOption;
import com.binance.dex.api.client.domain.broadcast.Transfer;
import com.binance.dex.api.client.encoding.AddressFormatException;
import com.binance.dex.api.client.encoding.Bech32;
import com.binance.dex.api.client.encoding.Bech32.Bech32Data;

import io.basquiat.bnb.domain.RequestWithdraw;
import io.basquiat.bnb.domain.entity.BlockTransactions;
import io.basquiat.bnb.domain.response.AddressResponse;
import io.basquiat.bnb.domain.response.BalanceResponse;
import io.basquiat.bnb.domain.response.ValidationAddressResponse;
import io.basquiat.bnb.domain.response.WithdrawResponse;
import io.basquiat.bnb.repository.BlockTransactionsRepository;
import io.basquiat.common.code.CategoryCode;
import io.basquiat.common.code.CommonCode;
import io.basquiat.common.code.ResultCode;
import io.basquiat.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 
 * binance coin service
 * 
 * 전송 관련은 restClient를 사용한다.
 * 이유는 node같은 경우에는 발란스가 다른 방식으로 나오는 경우가 있어서 게더링이 되지 않는 경우가 발생
 * 
 * created by basquiat
 *
 */
@Service("bnb")
@Slf4j
public class BnbService {

	@Value("${bnb.prefix}")
	private String BNB_PREFIX;

	@Autowired
	private Wallet wallet;

	@Autowired
	@Qualifier("restClient")
	private BinanceDexApiRestClient restClient;

	@Autowired
	private AsyncService async;

	@Autowired
	private BlockTransactionsRepository blockTransactionsRepo;

	/**
	 * 주소 생성
	 * 다른 거래소를 벤치마킹하면 리플처럼 하나의 대표주소에 메모를 활용한다.
	 * bnb에서 트랜잭션을 생성할 때 메모 정보를 넣을 수 있다.
	 * 입금 스케쥴에서 이 메모 정보를 통해 어떤 사용자의 주소로 넘어왔는지 체크할 수 있다.
	 * 이 어플리케이션은 단순한 프록시 역할만 할 것이다. 따라서 메모 정보는 요청한 클라이언트에서 생성하도록 하자.
	 * @return Mono<AddressResponse>
	 */
	public Mono<AddressResponse> createAddress() {
		String address = wallet.getAddress();
		String privateKey = wallet.getPrivateKey();
		// 그럴 일이 없겠지만 어떤 세팅 문제로 이 값들이 없다면 에러로 간주하자.
		if(StringUtils.isEmpty(address) && StringUtils.isEmpty(privateKey)) {
			return Mono.error(new RuntimeException("[Create Address] ERROR"));
		}
		
		log.info("[Create Address] END");
		return Mono.just(AddressResponse.builder().resultCode(ResultCode.CREATEWALLET_OK.statusCode)
												  .address(address)
												  .privateKey(privateKey)
												  .message(ResultCode.CREATEWALLET_OK.message)
												  .build());
	}

	/**
	 * 주소 유효성 체크, bnb라이브러리에서 제공하는 Bech32로 디코딩했을 때 prefix를 대조해서 성공 여부.
	 * 주소가 유효하지 않으면 AddressFormatException -> Checksum does not validate 메세지를 던진다.
	 * @param address
	 * @return Mono<ValidationAddressResponse>
	 */
	public Mono<ValidationAddressResponse> checkAddress(String address) {
		ValidationAddressResponse validationAddressResponse = null;
		try {
			Bech32Data bech32Data = Bech32.decode(address);
			if(BNB_PREFIX.equals(bech32Data.getHrp())) {
				validationAddressResponse = ValidationAddressResponse.builder()
																	 .resultCode(ResultCode.CHECKADDR_OK.statusCode)
																	 .message(ResultCode.CHECKADDR_OK.message)
																	 .build();
			}
		} catch(AddressFormatException afe) {
			validationAddressResponse = ValidationAddressResponse.builder()
																 .resultCode(ResultCode.CHECKADDR_ERROR.statusCode)
																 .message(ResultCode.CHECKADDR_ERROR.message)
																 .build();
			log.error("[Create Address Validation Check] ERROR " + afe.getMessage());
		}
		log.info("[Create Address Validation Check] END");
		return Mono.just(validationAddressResponse);
	}

	/**
	 * 출금 지갑 주소 잔고 조회
	 * @return Mono<BalanceResponse>
	 */
	public Mono<BalanceResponse> getBalanceOf() {
		log.info("[Inquire Address Balance] address : " + wallet.getAddress());
		Mono<Account> account = Mono.empty();
		// 노드쪽 연결하는 부분에서 에러가 났을 경우 조회가 되지 않을테니 try catch로 묶어서 에러를 컨트롤러 쪽으로 넘겨준다. 
		try {
			account = async.excute(() -> restClient.getAccount(wallet.getAddress()));
		} catch(Exception e) {
			return Mono.error(new RuntimeException("[Inquire Address Balance] ERROR"));
		}
		return account.map(mapper -> 
								{
									// BNB 코인은 내부적으로 여러개의 코인을 가질 수 있다. 이유는 거래 관련 스왑에 대한 처리를 하는데
									// 이 때문에 발란스 객체가 리스트로 존재한다.
									// 그 중에 우리는 BNB 코인의 발란스만 필요하기 때문에 리스트에서 심볼이 BNB인 녀석을 찾아서 해당 발란스를 가져온다.
									Double amount = mapper.getBalances().stream().filter(balance -> CommonCode.COINSYMBOL.value.equals(balance.getSymbol()))
																			  	 .map(balance -> balance.getFree())
																			  	 .mapToDouble(Double::parseDouble).sum();
									return BalanceResponse.builder().resultCode(ResultCode.BALANCE_OK.statusCode)
																	.amount(BigDecimal.valueOf(amount))
																	.address(wallet.getAddress())
																	.message(ResultCode.BALANCE_OK.message)
																	.build();
								}		
				
						  );
	}

	/**
	 * 출금
	 * @param reqeustWithdraw
	 * @return Mono<WithdrawResponse>
	 */
	public Mono<WithdrawResponse> startWithdraw(RequestWithdraw requestWithdraw) {
		log.info("[Withdraw Binance Coin] Request Data : " + CommonUtils.convertJsonStringFromObject(requestWithdraw));
		// 넘어온 프라이빗 키가 실제 지갑 프라이빗키와 같지 않으면 리턴해야한다.
		if(!wallet.getPrivateKey().equals(requestWithdraw.getPrivateKey())) {
			log.info("Request Data privateKye Check -----> not Match privateKey.");
			return Mono.error(new RuntimeException("ot Match privateKey."));
		}
		
		Transfer transfer = this.createTrasfer(requestWithdraw);
		TransactionOption options = TransactionOption.DEFAULT_INSTANCE;
		options.setMemo(requestWithdraw.getMemo()); // bnb destination tag like ripple
		
		Mono<List<TransactionMetadata>> response = this.transfer(transfer, options)
													   .doOnSuccess(listTm -> 
																	{
																		log.info("[Withdraw Binance Coin] SUCCESS");
																		TransactionMetadata transactionMetadata = listTm.get(0);
																		BlockTransactions blockTransactions = this.createBlockTransactions(transactionMetadata, requestWithdraw);
																		async.excuteJDBC(() -> blockTransactionsRepo.save(blockTransactions)).subscribe();
																	}
													   );
		
		return response.map(list -> list.get(0))
					   .map(tm -> 
		   					  { 
		   						  // 여기까지 왔다면 텔레그램으로 출금 정보 메세지를 보낸다.
		   						  return WithdrawResponse.builder().resultCode(ResultCode.WITHDRAW_OK.statusCode)
		   								  						   .message(ResultCode.WITHDRAW_OK.message)
		   								  						   .txid(tm.getHash())
		   								  						   .transactionFee(0)
		   								  						   .build();
			   				  }
					   );
	}

	/** =============================================== service 내부 private Method Area =============================================== */
	
	/**
	 * 노드로 전송 요청을 한다.
	 * @param transfer
	 * @param options
	 * @return Mono<List<TransactionMetadata>>
	 */
	private Mono<List<TransactionMetadata>> transfer(Transfer transfer, TransactionOption options) {
		// 전송이 성공하면 doOnSuccess를 통해 block_transactions 테이블에 관련 정보를 인서트 하자.
		// 리스트인 이유는 바이낸스 블록체인이 멀티 transfer를 지원하기 때문이다.
		// 싱글 트랜스퍼의 리턴 역시 리스트이지만 사이즈가 1이기 때문에 첫 번째 인덱스를 가져오면 된다.
		return async.excute(() -> restClient.transfer(transfer, wallet, options, true));
	}

	/**
	 * 전송 정보 생성하기.
	 * @param requestWithdraw
	 * @return Transfer
	 */
	private Transfer createTrasfer(RequestWithdraw requestWithdraw) {
		Transfer transfer = new Transfer();
		transfer.setCoin(CommonCode.COINSYMBOL.value);
		transfer.setAmount(requestWithdraw.getAmount());
		transfer.setFromAddress(wallet.getAddress());
		transfer.setToAddress(requestWithdraw.getRequestAddress());
		return transfer;
	}

	/**
	 * block_transactions 테이블에 인서트할 정보를 생성하기.
	 * @param transactionMetadata
	 * @param requestWithdraw
	 * @return BlockTransactions
	 */
	private BlockTransactions createBlockTransactions(TransactionMetadata transactionMetadata, RequestWithdraw requestWithdraw) {
		return BlockTransactions.builder().txHash(transactionMetadata.getHash())
										  .txType(CategoryCode.TRANSFER.code)
										  .action(CategoryCode.SEND.code)
										  .toAddress(requestWithdraw.getRequestAddress())
										  .fromAddress(wallet.getAddress())
										  .value(requestWithdraw.getAmount())
										  .txAsset(CommonCode.COINSYMBOL.value)
										  .memo(requestWithdraw.getMemo())
										  .timeStamp(new Date())
										  .build();
	}

}
