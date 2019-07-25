package io.basquiat.common.code;

/**
 * 
 * CategoryCode
 *  
 * created by basquiat
 *
 */
public enum CategoryCode {

	SEND("send"),
	
	RECEIVE("receive"),
	
	/** txType */
	TRANSFER("transfer");
	
	/** code */
	public String code;
	
	/**
	 * constructor
	 * @param code
	 */
	CategoryCode(String code) {
		this.code = code;
	}

}
