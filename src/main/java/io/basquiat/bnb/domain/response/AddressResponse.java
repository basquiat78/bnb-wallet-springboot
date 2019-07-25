package io.basquiat.bnb.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 주소 생성 응답 도메인
 * 
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
	
	/** 결과 코드 */
	private String resultCode;
	
	/** private key */
	private String privateKey;
	
	/** bnb wallet address */
	private String address;
	
	/** message */
	private String message;

}
