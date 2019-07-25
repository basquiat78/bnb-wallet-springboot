package io.basquiat.bnb.domain.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 출금 지갑 발란스 조회 응답 도메
 * 
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {

	/** 결과 응답 코드 */
	private String resultCode;
	
	/** message */
	private String message;
	
	/** 출금 지갑 잔고 수량 */
	private BigDecimal amount;
	
	/** 출금 지갑 주소 */
	private String address;
	
}
