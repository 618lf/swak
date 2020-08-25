package com.swak.vertx.protocol.ws;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

/**
 * 
 * @author lifeng
 * @date 2020年8月25日 下午2:26:45
 */
public class WebSocketHandlerAdapter implements WebSocketHandler {

	@Override
	public void onConnect(ServerWebSocket socket) {

	}

	@Override
	public void onMessage(ServerWebSocket socket, WebSocketFrame message) {

	}

	@Override
	public void onException(ServerWebSocket socket, Throwable e) {

	}

	@Override
	public void onClose(ServerWebSocket socket) {

	}
}
