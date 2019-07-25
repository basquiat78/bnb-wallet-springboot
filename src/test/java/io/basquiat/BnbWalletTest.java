package io.basquiat;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.broadcast.TransactionOption;
import com.binance.dex.api.client.domain.broadcast.Transfer;

import io.basquiat.bnb.domain.response.BalanceResponse;
import io.basquiat.bnb.service.BnbService;
import io.basquiat.common.code.CommonCode;
import io.basquiat.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class BnbWalletTest {
	
	@Autowired
	private BnbService bnb;

	private String testBnbAddress;
	
	private String requestBnbAddress;
	
	private String invalidBnbAddress;
	
	@Autowired
	@Qualifier("restClient")
	private BinanceDexApiRestClient restClient;
	
	@Before
    public void init() {
		testBnbAddress = "tbnb17rg2daudfezctq7c40rl04p0jktkglqczsjy6l";
		requestBnbAddress = "tbnb142sx5dga3ruu72w4s5puftvr4f4hgm9ah5k8g6"; // 유효한 주소
		invalidBnbAddress = "tbnb142sx5dga3ruu72w4s5puftvr4f4hgm9ah5k8g7"; // 유효하지 않은 주소
    }
	
	/**
	 * 주소 유효성 체크
	 */
	//@Test
	public void bnbWalletTest1() {
		bnb.checkAddress(testBnbAddress).subscribe(var -> log.info(CommonUtils.convertJsonStringFromObject(var)));
		bnb.checkAddress(requestBnbAddress).subscribe(var -> log.info(CommonUtils.convertJsonStringFromObject(var)));
		bnb.checkAddress(invalidBnbAddress).subscribe(var -> log.info(CommonUtils.convertJsonStringFromObject(var)));
	}
	
	/**
	 * 주소 생성
	 */
	//@Test
	public void bnbWalletTest2() {
		bnb.createAddress().subscribe(wr -> log.info(CommonUtils.convertJsonStringFromObject(wr)));
	}
	
	/**
	 * 출금 주소 발란스 정보 
	 */
	//@Test
	public void bnbWalletTest3() {
		Mono<BalanceResponse> mono = bnb.getBalanceOf();
		StepVerifier.create(mono)
					.expectNextMatches(balance -> testBnbAddress.equals(balance.getAddress()))
					.verifyComplete();
	}
	
	/**
	 * 출금 테스트
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	@Test
	public void bnbWalletTest4() throws NoSuchAlgorithmException, IOException {
		String toAddress = testBnbAddress;
		String privateKey = "";
		Wallet wallet = new Wallet(privateKey, BinanceDexEnvironment.TEST_NET);
		Transfer transfer = new Transfer();
		transfer.setCoin(CommonCode.COINSYMBOL.value);
		transfer.setAmount("0.01");
		transfer.setToAddress(toAddress);
		transfer.setFromAddress(wallet.getAddress());
		TransactionOption options = TransactionOption.DEFAULT_INSTANCE;
		options.setMemo("Basquiat");
		restClient.transfer(transfer, wallet, options, true);
	
	}
	
}
