package io.basquiat.websocket.config;

import org.springframework.context.ApplicationEvent;

/**
 * ReconnectEvent
 * created by basquiat
 *
 */
public class ReconnectEvent extends ApplicationEvent {

	private static final long serialVersionUID = 6206850374669884748L;

	private String method;
	
	/**
	 * constructor
	 * @param method
	 */
	public ReconnectEvent(String method) {
		super(method);
		this.method = method;
	}
	
	/**
	 * get method
	 * @return String
	 */
	public String method() {
		return this.method;
	}

}
