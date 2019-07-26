package io.basquiat.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.basquiat.bnb.service.DepositService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 
 * Publish Event 설정
 * 
 * created by basquiat
 *
 */
@Slf4j
@Component
public class EventConfiguration {

	@Autowired
	private DepositService depositService;
	
	/**
	 * txHash를 받으면 event처리
	 * @param event
	 * @throws Exception 
	 */
	@EventListener(FindAndSaveEvent.class)
	public void findAndSaveEvent(FindAndSaveEvent event) throws Exception{
		log.info("FindAndSaveEvent Catch ---> next Process Start");
		// 트랜잭션이 발생하고 나서 들어오는 메세지를 처리하는 동안 api에서 해당 트랜잭션 정보가 아직 올라가지 않는 상황이 발생한다.
		// 10초 정도 딜레이를 준다.
		Mono.delay(Duration.ofSeconds(10)).subscribe(s -> depositService.findAndSave(event.txHash()));
	}
	
}
