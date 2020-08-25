package com.swak.vertx.protocol.ws;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

/**
 * WebSocket 山下文
 * 
 * @author lifeng
 * @date 2020年8月25日 下午2:34:08
 */
public class WebSocketContext {
	final ServerWebSocket socket;
	final WebSocketFrame message;

	public WebSocketContext(ServerWebSocket socket, WebSocketFrame message) {
		this.socket = socket;
		this.message = message;
	}

	public ServerWebSocket getSocket() {
		return socket;
	}

	public WebSocketFrame getMessage() {
		return message;
	}
}
