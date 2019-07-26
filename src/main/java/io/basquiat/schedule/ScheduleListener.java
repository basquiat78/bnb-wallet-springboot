package io.basquiat.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.basquiat.schedule.service.ReceiveScheduleService;

/**
 * 
 * Schedule Listener
 * 
 * created by basquiat
 *
 */
@Component
public class ScheduleListener {

	@Autowired
	private ReceiveScheduleService receiveScheduleService;

	/**
	 * app이 뜰 때 스케쥴을 구동시킨다.
	 */
	//@EventListener(ApplicationReadyEvent.class)
	public void depositChekcScheduling() {
		receiveScheduleService.start();
	}

}
