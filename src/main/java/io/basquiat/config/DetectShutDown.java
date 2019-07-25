package io.basquiat.config;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import io.basquiat.bnb.domain.LastBlockHeight;
import io.basquiat.bnb.domain.LastBlockHeightStore;
import io.basquiat.utils.FileIOUtils;

/**
 * 
 * 서버를 끌때 메모리에 있는 최신 블록 정보를 파일로 쓴다.
 * 
 * created by basquiat
 *
 */
@Component
public class DetectShutDown {

	/**
	 * 서버가 내려갈때 실행한다.
	 * @throws Exception
	 */
	@PreDestroy
	public void onDestroy() throws Exception {
		//서버가 내려갈때 lastBlockHeight에 저장되어 있는 체크한 마지막 블록 정보를 파일에 저장한다.
		LastBlockHeight lastBlockHeight = LastBlockHeightStore.getLastBlockHeight();
		FileIOUtils.writeFile(lastBlockHeight);
	}

}
