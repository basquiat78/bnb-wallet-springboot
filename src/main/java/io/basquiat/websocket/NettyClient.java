package io.basquiat.websocket;

import java.net.URI;

import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.binance.dex.api.client.Wallet;

import io.basquiat.bnb.service.ReceiveService;
import io.basquiat.common.code.BeanNameCode;
import io.basquiat.common.code.CategoryCode;
import io.basquiat.utils.CommonUtils;
import io.basquiat.websocket.config.ReconnectEvent;
import io.basquiat.websocket.handler.ClientHandler;
import io.basquiat.websocket.handler.SessionHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;

/**
 * 
 * NettyClient
 * 
 * created by basquiat
 *
 */
@Slf4j
public class NettyClient {
	
	private ApplicationContext context;
	
	private ReceiveService receiveService;
	
	/**
	 * constructor
	 */
	public NettyClient() { }
	
	/**
	 * constructor with param
	 * @param context
	 */
	public NettyClient(ApplicationContext context) {
		this.context = context;
		this.receiveService = (ReceiveService) context.getBean(BeanNameCode.RECEIVESERVICE.beanName);
	}
	
	public Disposable start(WebSocketClient webSocketClient, URI uri, ClientHandler clientHandler) {		
		
		clientHandler.connected()
					 .subscribe(this::doNettyClient);
		
		Disposable clientConnection = webSocketClient.execute(uri, clientHandler)
													 .subscribeOn(Schedulers.elastic())
													 .subscribe();
		return clientConnection;
	}
	
	private void doNettyClient(SessionHandler sessionHandler) {
		
		Wallet wallet = (Wallet) context.getBean(BeanNameCode.WALLET.beanName);
		
		Message msg = Message.builder().method(CategoryCode.SUBSCRIBE.code)
										   .topic(CategoryCode.TRANSFERS.code)
										   .address(wallet.getAddress())
										   .build();
		
		sessionHandler.connected()
					  .doOnNext(session -> log.info("Client Connected. Session Id is {}", session.getId()))
					  .map(session -> CommonUtils.convertJsonStringFromObject(msg))
					  .doOnNext(message -> sessionHandler.send(message))
					  .subscribe(message -> log.info("Client Send Message is {}", message));
		
		sessionHandler.disconnected()
					  .subscribe(session -> 
				  				{
				  					log.info("Client Disconnected. Session Id is {}", session.getId());
				  					log.info("Reconnect WebSocket START");
				  					context.publishEvent(new ReconnectEvent(CategoryCode.RECONNECT.code));
				  				}
					  );
						
		sessionHandler.receive()
					  .subscribeOn(Schedulers.elastic())
			  		  .subscribe(message -> receiveService.process(message));
	}

}
