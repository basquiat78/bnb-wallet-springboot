package io.basquiat.websocket.handler;

import java.nio.channels.ClosedChannelException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.core.publisher.ReplayProcessor;
import reactor.netty.channel.AbortedException;

/**
 * 
 * WebSocket Session Handler
 * 일반적인 방식과는 다르게 WebFlux에 맞는 설정을 하기 위해서는 다음과 같은 Processor설정을 해줘야 한다.
 * 
 * non singleton방식으로 가져가기 위해 스코프를 prototype으로 가져간다.
 * 
 * created by basquiat
 *
 */
@Component
@Scope("prototype")
public class SessionHandler {

	private final ReplayProcessor<String> receiveProcessor;
	
	private final MonoProcessor<WebSocketSession> connectedProcessor;
	
	private final MonoProcessor<WebSocketSession> disconnectedProcessor;
	
	/** 웹소켓 연결 여부 */
	private boolean webSocketConnected;
	
	/** 웹소켓 세션 */
	private WebSocketSession session;	
	
	/**
	 * constructor
	 */
	public SessionHandler() {
		this(50);
	}
	
	/**
	 * 각 프로세스 초기화
	 * ReplayProcessor의 경우에는 캐쉬 사이즈를 받는다.
	 * @param historySize
	 */
	public SessionHandler(int historySize) {
		receiveProcessor = ReplayProcessor.create(historySize);
		connectedProcessor = MonoProcessor.create();
		disconnectedProcessor = MonoProcessor.create();
		webSocketConnected = false;
	}
	
	/**
	 * 
	 * @param session
	 * @return Mono<Void>
	 */
	protected Mono<Void> handle(WebSocketSession session) {
		
		this.session = session;
		// 해당 세션으로부터 받은 메세지 처리 프로세스
		Flux<String> receive = session.receive()
									  .map(message -> message.getPayloadAsText())
									  .doOnNext(textMessage -> receiveProcessor.onNext(textMessage))
									  .doOnComplete(() -> receiveProcessor.onComplete());
		
		// 커넥션 프로세스
		Mono<Object> connected = Mono.fromRunnable(() -> 
														{
															webSocketConnected = true;
															connectedProcessor.onNext(session);
														}
												  );

		// 디스커넥션 프로세스
		Mono<Object> disconnected = Mono.fromRunnable(() -> 
															{	
																webSocketConnected = false;
																disconnectedProcessor.onNext(session);
															}
													 )
										.doOnNext(value -> receiveProcessor.onComplete());
			
		return connected.thenMany(receive).then(disconnected).then();
	}
	
	/**
	 * connectionProcessor 객체 반환
	 * @return Mono<WebSocketSession>
	 */
	public Mono<WebSocketSession> connected() {
		return connectedProcessor;
	}
	
	/**
	 * disconnectionProcessor 객체 반환
	 * @return Mono<WebSocketSession>
	 */
	public Mono<WebSocketSession> disconnected() {
		return disconnectedProcessor;
	}
	
	/**
	 * connectionProcessor 객체 반환
	 * @return Mono<WebSocketSession>
	 */
	public Mono<WebSocketSession> reconnected() {
		return connectedProcessor;
	}
	
	/**
	 * connection 여부 반환
	 * @return boolean
	 */
	public boolean isConnected() {
		return webSocketConnected;
	}

	/**
	 * receiveProcessor 반환
	 * @return Flux<String>
	 */
	public Flux<String> receive() {
		return receiveProcessor;
	}
	
	/**
	 * send message
	 * @param message
	 */
	public void send(String message) {
		if(webSocketConnected) {
			session.send(Mono.just(session.textMessage(message)))
				   .doOnError(ClosedChannelException.class, t -> connectionClosed())
				   .doOnError(AbortedException.class, t -> connectionClosed())
				   .onErrorResume(ClosedChannelException.class, t -> Mono.empty())
				   .onErrorResume(AbortedException.class, t -> Mono.empty())
				   .subscribe();
		}	
	}
	
	/**
	 * connection close
	 */
	private void connectionClosed() {
		if(webSocketConnected) {
			webSocketConnected = false;
			disconnectedProcessor.onNext(session);
		}
	}

}
