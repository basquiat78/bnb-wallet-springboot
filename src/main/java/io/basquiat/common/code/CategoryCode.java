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
	TRANSFER("transfer"),
	
	/** receive object key */
	STREAM("stream"),
	
	/** receive object key */
	DATA("data"),
	
	/** receive object key */
	T("t"),
	
	/** receive object key */
	O("o"),
	
	/** receive object key */
	H("H"),
	
	/** method */
	SUBSCRIBE("subscribe"),
	
	/** method */
	GET("GET"),
	
	/** method */
	RECONNECT("reconnect"),

	/** websocket stream type */
	TRANSFERS("transfers");
	
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
