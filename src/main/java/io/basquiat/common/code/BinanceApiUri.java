package io.basquiat.common.code;

/**
 * 
 * BinanceApiUri
 * 
 * created by basquiat
 *
 */
public enum BinanceApiUri {

	/** transaction */
	TRANSACTIONS("/api/v1/transactions?address=", "트랜잭션스 조회");
	
	/** uri */
	public String URI;
	
	/** uri 설명 */
	public String description;

	/**
	 * enum constructor
	 * @param uri
	 * @param description
	 */
	BinanceApiUri(String uri, String description) {
		this.URI = uri;
		this.description = description;
	}
	
}
