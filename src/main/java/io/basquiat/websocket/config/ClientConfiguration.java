package io.basquiat.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

/**
 * 
 * server가 아닌 바이낸스 서버에 붙을 클라이언트 사이드 설정
 * 
 * created by basquiat
 *
 */
@Configuration
public class ClientConfiguration {

	/**
	 * 
	 * 심플하게 ReactorNettyWebsocketClient를 빈으로 올린다.
	 * 
	 * @return WebSocketClient
	 */
	@Bean
	public WebSocketClient webSocketClient() {
		return new ReactorNettyWebSocketClient();
	}

}
