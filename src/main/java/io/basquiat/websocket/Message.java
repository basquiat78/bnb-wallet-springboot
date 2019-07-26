package io.basquiat.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Websocket연결시 보낼 메세지 도메인
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
	
	/** method */
	private String method;
	
	/** topic */
	private String topic;
	
	/** address */
	private String address;

}
