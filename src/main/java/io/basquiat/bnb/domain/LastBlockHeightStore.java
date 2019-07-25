package io.basquiat.bnb.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * deposit 정보를 체크하기 위한 블록 정보를 담는 맵
 * created by basquiat
 *
 */
public class LastBlockHeightStore {

	private static final Map<String, LastBlockHeight> LAST_BLOCK_HEIGHT_STORE = new ConcurrentHashMap<>();
	
	/**
	 * LAST_BLOCK_HEIGHT_STORE을 map에 담는다.
	 * @param lastBlockHeight
	 */
	public static void setLastBlockHeigh(LastBlockHeight lastBlockHeight) {
		LAST_BLOCK_HEIGHT_STORE.put(LastBlockHeight.class.getSimpleName(), lastBlockHeight);
	}
	
	/**
	 * LAST_BLOCK_HEIGHT_STORE에서 block를 가져온다.
	 * @param key
	 * @return LastBlockHeight
	 */
	public static LastBlockHeight getLastBlockHeight() {
		return LAST_BLOCK_HEIGHT_STORE.get(LastBlockHeight.class.getSimpleName());
	}
	
}
