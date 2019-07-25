package io.basquiat.common.code;

/**
 * 
 * result code
 * 
 * created by basquiat
 *
 */
public enum ResultCode {

	/** 지갑 생성 응답 코드 */
	CREATEWALLET_OK("OK", "지갑 생성 성공"),
	
	CREATEWALLET_ERROR("FAIL", "지갑 생성 실패"),
	
	CHECKADDR_OK("OK", "Valid Bnb Address"),
	
	CHECKADDR_ERROR("FAIL", "Invalid Bnb Address"),
	
	BALANCE_OK("OK", "출금 지갑잔고 조회 성공"),
	
	BALANCE_ERROR("FAIL", "출금 지갑잔고 조회 실패"),
	
	WITHDRAW_OK("OK", "출금 성공"),
	
	WITHDRAW_ERROR("FAIL", "출금 실패");
	
	/** 상태 코드 */
	public String statusCode;
	
	/** 메세지 */
	public String message;
	
	/**
	 * constructor
	 * @param statusCode
	 * @param message
	 */
	ResultCode(String statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

}
