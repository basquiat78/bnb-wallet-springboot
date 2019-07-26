package io.basquiat.common.code;

import java.util.Arrays;
import java.util.function.Function;

import com.binance.dex.api.client.BinanceDexEnvironment;

/**
 * environmentCode Enum
 * created by basquiat
 *
 */
public enum EnvironmentCode {


	TEST("tbnb", value -> BinanceDexEnvironment.TEST_NET.getBaseUrl()),
	
	PROD("bnb", value -> BinanceDexEnvironment.PROD.getBaseUrl());
	
	public String prefix;
	
	private Function<String, String> expression;

	/**
	 * constructor
	 * @param prefix
	 * @param expression
	 */
	EnvironmentCode(String prefix, Function<String, String> expression) {
		this.prefix = prefix;
		this.expression = expression;
	}

	public String getApiURL(String value) {
		return expression.apply(value);
	}
	
	public static EnvironmentCode fromString(String prefix) {
		return Arrays.asList(EnvironmentCode.values()).stream()
											   .filter( EnvironmentCode -> EnvironmentCode.prefix.equalsIgnoreCase(prefix) )
											   .map(mapper -> mapper)
											   .findFirst().orElse(null);
    }
}
