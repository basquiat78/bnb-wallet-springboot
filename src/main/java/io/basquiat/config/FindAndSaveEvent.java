package io.basquiat.config;

import org.springframework.context.ApplicationEvent;

/**
 * FindAndSaveEvent
 * created by basquiat
 *
 */
public class FindAndSaveEvent extends ApplicationEvent {

	private static final long serialVersionUID = -4509048078647343166L;
	
	private String txHash;
	
	/**
	 * constructor
	 * @param txHash
	 */
	public FindAndSaveEvent(String txHash) {
		super(txHash);
		this.txHash = txHash;
	}
	
	/**
	 * get txHash
	 * @return String
	 */
	public String txHash() {
		return this.txHash;
	}

}
