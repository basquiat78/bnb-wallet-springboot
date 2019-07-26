package io.basquiat.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.binance.dex.api.client.Wallet;

import io.basquiat.websocket.config.ReconnectEvent;
import io.basquiat.websocket.handler.ClientHandler;

/**
 * WebSocket PostContruct
 * created by basquiat
 *
 */
@Component
public class WebSocketListener {
	
	@Value("${bnb.wss.url}")
	private String BNB_WSS_URL;
	
	@Autowired
	private WebSocketClient webSocketClient;
	
	@Autowired
	private Wallet wallet;
	
	@Autowired
	private ApplicationContext context;

	/**
	 * websocket start
	 * @throws URISyntaxException
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void start() throws URISyntaxException {
		NettyClient nettyClient = new NettyClient(context);
		nettyClient.start(webSocketClient, new URI(BNB_WSS_URL + wallet.getAddress()), new ClientHandler());
	}
	
	/**
	 * websocket이 끊어졌을때 다시 스타트
	 * @param event
	 * @throws URISyntaxException
	 */
	@EventListener(ReconnectEvent.class)
	public void reconnectEvent(ReconnectEvent event) throws URISyntaxException {
		NettyClient nettyClient = new NettyClient(context);
		nettyClient.start(webSocketClient, new URI(BNB_WSS_URL + wallet.getAddress()), new ClientHandler());
	}
	
}
