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
 * BNB mainnet을 따른다. 
 * 
 * created by basquiat
 *
 */
@Configuration
@Profile("mainnet")
public class BinanceMainnetConfiguration {

	private BinanceDexApiNodeClient nodeClient;
	
	private BinanceDexApiRestClient restClient;
	
	private Wallet wallet;
	
	/**
	 * BinanceDexApiRestClient, BinanceDexApiNodeClient Wallet initialize from private key
	 * @param bnbPrivateKey
	 * @throws IOException
	 */
	public BinanceMainnetConfiguration(@Value("${bnb.private.key}") final String bnbPrivateKey) throws IOException {
		nodeClient = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(BinanceDexEnvironment.PROD.getNodeUrl(), BinanceDexEnvironment.PROD.getHrp());
		restClient = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.PROD.getBaseUrl());
		wallet = new Wallet(bnbPrivateKey, BinanceDexEnvironment.PROD);
	}
	
//	/**
//	 * BinanceDexApiRestClient, BinanceDexApiNodeClient Wallet initialize from private key
//	 * @param mnemonic
//	 * @throws IOException
//	 */
//	public BinanceConfiguration(@Value("${bnb.private.key}") final String bnbPrivateKey) throws IOException {
//		nodeClient = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(BinanceDexEnvironment.PROD.getNodeUrl(), BinanceDexEnvironment.PROD.getHrp());
//		restClient = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.PROD.getBaseUrl());
//		List<String> mnemonicList = Arrays.asList(menmonic.splist(" "));
//		wallet = Wallet.createWalletFromMnemonicCode(mnemonicList, BinanceDexEnvironment.PROD);
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
	@Bean
	public Wallet wallet() {
		return this.wallet;
	}

}
