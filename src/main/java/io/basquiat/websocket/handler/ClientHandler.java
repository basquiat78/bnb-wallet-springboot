package io.basquiat.websocket.handler;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

/**
 * 
 * client Handler
 * 
 * created by basquiat
 *
 */
public class ClientHandler implements WebSocketHandler {
	
	private final SessionHandler sessionHandler;
	
	private final ReplayProcessor<SessionHandler> connectedProcessor;

	/**
	 * Constructor
	 */
	public ClientHandler() {	
		sessionHandler = new SessionHandler();
		connectedProcessor = ReplayProcessor.create();
	}

	@Override
	public Mono<Void> handle(WebSocketSession session) {		
		sessionHandler.connected()
					  .doOnNext(value -> connectedProcessor.onNext(sessionHandler))
					  .subscribe();
		return sessionHandler.handle(session);
	}

	/**
	 * connectedProcessor 반환
	 * @return Flux<SessionHandler>
	 */
	public Flux<SessionHandler> connected() {
		return connectedProcessor;
	}

}