package io.basquiat.bnb.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 주소 유효성 체크 응답 도메인
 * 
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationAddressResponse {

	/** 응답 코드 */
	private String resultCode;
	
	/** message */
	private String message;

}
