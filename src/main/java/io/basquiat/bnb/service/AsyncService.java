package io.basquiat.bnb.service;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 
 * Webflux의 Flow는 전체 비동기여야 한다.
 * 하지만 외부 API를 WebClient가 아닌 방식으로 (BNB 라이브러리를 이용한 방식)이라든가 mySql같은 JDBC의 경우에는 동기로 Blocking이 된다.
 * 두가지 방법이 있는데 하나는 Callable하게 동기 코드를 비동기로 변환한다.
 * 
 * 1. Mono.fromCallable 방식
 * 2. Java8의 CompletableFuture를 이용한 supplyAsync방식
 * 
 *  2의 경우에는 Mono.fromFuture로 받아칠 수 있지만 fromCallable을 지원하기 떄문에 코드 흔적만 남긴다.
 * 
 * created by basquiat
 *
 */
@Service("asyncService")
public class AsyncService {

	/**
	 * sync -> async using
	 * 차후 jpa를 위해 사용할 코드
	 * @param <T>
	 * @param callable
	 * @return Mono<T>
	 */
	public <T> Mono<T> excute(Callable<T> callable) {
		return Mono.subscriberContext().flatMap(context -> 
													{
														return Mono.fromCallable(callable).subscribeOn(Schedulers.elastic());
													}
											   );
    }

	/**
	 * 동기식 api를 비동기 호출로 변환한다.
	 * @param <T>
	 * @param callable
	 * @return CompletableFuture<T>
	 */
	public <T> CompletableFuture<T> excuteCF(Supplier<T> callable) {
		return CompletableFuture.supplyAsync(callable);
	}
	
}
