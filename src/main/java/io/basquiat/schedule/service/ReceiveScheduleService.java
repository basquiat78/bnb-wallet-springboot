package io.basquiat.schedule.service;

import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import io.basquiat.bnb.service.ReceiveService;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * created by basquiat
 * 
 * 스케쥴 서비스. 시세를 가져오는 주기를 변경할 수 있는 서비스
 *
 */
@Slf4j
@Service("receiveScheduleService")
public class ReceiveScheduleService {

	@Value("${schedule.deposit.cron}")
	private String DEPOSIT_CRON_EXPRESSION;
	
	@Autowired
	private ReceiveService receiveService;
	
	@Autowired
	private TaskScheduler receiveScheduler;
	
	private ScheduledFuture<?> receiveScheduledFuture;

	/**
	 * schedule start
	 */
    public void start() {
    	ScheduledFuture<?> receiveScheduledFuture = this.receiveScheduler.schedule(() -> {
																							receiveService.schedulingBnbDepositCheck();
				                					  									 },
						                												new CronTrigger(DEPOSIT_CRON_EXPRESSION)
				                												  );
        this.receiveScheduledFuture = receiveScheduledFuture;
    }

    /**
     * 스케쥴링 주기를 변경한다.
     * 1. 기존의 돌고 스케쥴을 멈춘다.
     * 2. 넘어온 크론으로 다시 세팅한다.
     * 3. 다시 스케쥴을 구동시킨다.
     * 
     * @param cron
     */
    public void changeCronExpression(String cron) {
    	if(receiveScheduledFuture != null) {
    		receiveScheduledFuture.cancel(true);
    	}
        this.receiveScheduledFuture = null;
        log.info("schedule cron expression '" + this.DEPOSIT_CRON_EXPRESSION + "' change to '" + cron + "'");
        this.DEPOSIT_CRON_EXPRESSION = cron;
        this.start();
    }
    
}
