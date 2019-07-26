package io.basquiat.bnb.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Transactions 응답 도메인
 * 
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

	/** txHash */
	private String txHash;

	/** blockHeight */
	private long blockHeight;

	/** txType */
	private String txType;

	/** timeStamp */
	private String timeStamp;

	/** fromAddr */
	private String fromAddr;

	/** toAddr */
	private String toAddr;

	/** value */
	private String value;

	/** txAsset */
	private String txAsset;

	/** txFee */
	private String txFee;

	/** txAge */
	private int txAge;

	/** memo */
	private String memo;

}

