package io.basquiat.bnb.domain.response;

import java.util.List;

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
public class TransactionsResponse {

	/** tx */
	private List<Transaction> tx;

}

