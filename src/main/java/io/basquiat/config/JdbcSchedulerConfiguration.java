package io.basquiat.config;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * 
 * JDBC를 위한 커스텀 스케쥴러
 * 
 * created by basquiat
 *
 */
@Configuration
public class JdbcSchedulerConfiguration {

	@Value("${datasource.maximum-pool-size:100}")
	private int connectionPoolSize;

	/**
	 * custom scheduler for JDBC
	 * @return
	 */
	@Bean("jdbcScheduler")
	public Scheduler jdbcScheduler() {
		return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
	}

	/**
	 * transaction template configuration
	 * @param transactionManager
	 * @return
	 */
	@Bean
	public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
		return new TransactionTemplate(transactionManager);
	}

}
