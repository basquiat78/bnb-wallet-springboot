package io.basquiat.common.code;

/**
 * 
 * CommonCode Enum
 * 
 * created by basquiat
 *
 */
public enum CommonCode {

	COINSYMBOL("BNB"),
	
	TRANSACTIONFEE("0.000375");
	
	/** value */
	public String value;
	
	/**
	 * constructor
	 * @param value
	 */
	CommonCode(String value) {
		this.value = value;
	}

}
