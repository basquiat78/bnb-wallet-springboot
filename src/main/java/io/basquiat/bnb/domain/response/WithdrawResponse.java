package io.basquiat.bnb.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 출금 결과 응답 도메인
 * 
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawResponse {
	
	/** 결과 코드 */
	private String resultCode;
	
	/** message */
	private String message;

	/** transaction hash */
	private String txid;
	
	/** tranasction fee */
	private double transactionFee;

}
