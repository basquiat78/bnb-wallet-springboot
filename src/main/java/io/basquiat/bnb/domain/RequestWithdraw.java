package io.basquiat.bnb.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 출금 요청 정보
 * 
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestWithdraw {

	/** 사용자 요청 주소 */
	private String requestAddress;

	/**
	 * privateKey
	 * 실제로 거래소에서 이 키를 관리할 때는 일반적으로 암호화를 해서 보관하게 된다. 예를 들면 PKDF나 AES256을 이용한 암복화를 활용해서.
	 * 당연한 말이지만 출금 요청할 때는 내부에서 암호화된 키를 복화화해서 던져줘야한다.
	 * 해당 어플리케이션에 설정된 프라이빗 키와 비교하기 위해서이다.
	 * 무작정 요청했을 때 출금을 하게 된다면 문제가 발생할 수 있기 때문이다.
	 */
	private String privateKey;

	/** 수량 -> sdk로 전송할때 이 부분은 스트링으로 세팅하게 되어 있다. */
	private String amount;

	/**
	 * 메모 정보
	 * 확실히 해둘 것은 자신의 개인 지갑 주소라면 상관없지만 다른 거래소로의 전송이라면 필수로 넣어야 한다.
	 */
	private String memo;

}
