package com.swak.vertx.transport;

import com.swak.utils.StringUtils;
import com.swak.vertx.protocol.ws.ServerWebSocketHolder;
import com.swak.vertx.protocol.ws.WebSocketHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.ServerWebSocket;

/**
 * WebSocket服务
 * 
 * @author lifeng
 * @date 2020年4月23日 下午3:01:23
 */
public class ImServerVerticle extends AbstractVerticle implements Handler<ServerWebSocket> {

	private final String host;
	private final int port;
	private final WebSocketHandler handler;

	public ImServerVerticle(Object service, Class<?> type, String host, int port) {
		this.host = host;
		this.port = port;
		if (WebSocketHandler.class.isAssignableFrom(type)) {
			this.handler = (WebSocketHandler) service;
		} else {
			throw new RuntimeException("Service[Server.IM] Must Implements WebSocketHandler.");
		}
	}

	@Override
	public void start(Promise<Void> startPromise) {
		if (StringUtils.isBlank(host)) {
			vertx.createHttpServer().webSocketHandler(this).listen(port, res -> startPromise.complete());
		} else {
			vertx.createHttpServer().webSocketHandler(this).listen(port, host, res -> startPromise.complete());
		}
	}

	@Override
	public void handle(ServerWebSocket webSocket) {
		new ServerWebSocketHolder(webSocket, handler);
	}
}