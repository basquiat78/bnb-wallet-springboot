package io.basquiat.bnb.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 서버가 뜨거나 셧다운 될때 이 정보를 가지고 블록 체인으로부터 deposit 정보를 가지고 오게 된다.
 * 1. 서버가 뜰 때는 읽어서 이 블록과 블록체인의 jsonRpc을 때려 최근 블록을 읽어 올것이다.
 * 2. 루프를 돌면서 각 블록 하이트의 트랙잰션 정보를 읽기 위한 기준 정보를 담고 있으며
 *    파일로 저장하거나 읽어올 때 쓰는 객
 * 
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastBlockHeight {

	/** 라스트 블록 정보 */
	private long lastBlockHeight;
	
}
