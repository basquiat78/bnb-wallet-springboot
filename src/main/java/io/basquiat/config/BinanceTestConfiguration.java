package io.basquiat.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.binance.dex.api.client.BinanceDexApiClientFactory;
import com.binance.dex.api.client.BinanceDexApiNodeClient;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.Wallet;


/**
 * 
 * binance node configuration
 * 
 * profile 설정
 * 
 * BNB testnet을 따른다.
 * 
 * created by basquiat
 *
 */
@Configuration
@Profile("testnet")
public class BinanceTestConfiguration {

	private BinanceDexApiNodeClient nodeClient;
	
	private BinanceDexApiRestClient restClient;
	
	private Wallet wallet;
	
	/**
	 * BinanceDexApiNodeClient, BinanceDexApiRestClient, Wallet initialize from private key
	 * @param bnbPrivateKey
	 * @throws IOException
	 */
	public BinanceTestConfiguration(@Value("${bnb.private.key}") final String bnbPrivateKey) throws IOException {
		nodeClient = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(BinanceDexEnvironment.TEST_NET.getNodeUrl(), BinanceDexEnvironment.TEST_NET.getHrp());
		restClient = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
		wallet = new Wallet(bnbPrivateKey, BinanceDexEnvironment.TEST_NET);
	}
	
//	/**
//	 * BinanceDexApiAsyncRestClient, Wallet initialize from mnemonic
//	 * @param mnemonic
//	 * @throws IOException
//	 */
//	public BinanceConfiguration(@Value("${bnb.private.key}") final String bnbPrivateKey) throws IOException {
//		nodeClient = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(BinanceDexEnvironment.TEST_NET.getNodeUrl(), BinanceDexEnvironment.TEST_NET.getHrp());
//		restClient = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
//		List<String> mnemonicList = Arrays.asList(menmonic.splist(" "));
//		wallet = Wallet.createWalletFromMnemonicCode(mnemonicList, BinanceDexEnvironment.TEST_NET);
//	}
	
	/**
	 * create bean BinanceDexApiNodeClient
	 * @return BinanceDexApiNodeClient
	 */
	@Bean("nodeClient")
	public BinanceDexApiNodeClient nodeClient() {
		return this.nodeClient;
	}

	/**
	 * create bean "restClient"
	 * @return BinanceDexApiRestClient
	 */
	@Bean("restClient")
	public BinanceDexApiRestClient restClient() {
		return this.restClient;
	}
	
	/**
	 * wallet for credential
	 * @return Wallet
	 */
	@Bean("wallet")
	public Wallet wallet() {
		return this.wallet;
	}

}
