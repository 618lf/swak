package com.swak.vertx.transport;

import com.swak.utils.StringUtils;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.protocol.ws.ServerWebSocketHolder;
import com.swak.vertx.protocol.ws.WebSocketHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;

/**
 * WebSocket服务
 * 
 * @author lifeng
 * @date 2020年4月23日 下午3:01:23
 */
public class ImServerVerticle extends AbstractVerticle implements Handler<ServerWebSocket>, ServerVerticle {

	private final VertxProperties properties;
	private final WebSocketHandler handler;

	public ImServerVerticle(Object service, Class<?> type, VertxProperties properties) {
		this.properties = properties;
		if (WebSocketHandler.class.isAssignableFrom(type)) {
			this.handler = (WebSocketHandler) service;
		} else {
			throw new RuntimeException("Service[Server.IM] Must Implements WebSocketHandler.");
		}
	}

	@Override
	public void start(Promise<Void> startPromise) {

		// 服务器配置
		HttpServerOptions options = this.serverOptions(properties);

		// 发布服务
		if (StringUtils.isBlank(properties.getHost())) {
			vertx.createHttpServer(options).webSocketHandler(this).listen(properties.getImPort(),
					res -> this.startResult(startPromise, res));
		} else {
			vertx.createHttpServer(options).webSocketHandler(this).listen(properties.getImPort(), properties.getHost(),
					res -> this.startResult(startPromise, res));
		}
	}

	@Override
	public void handle(ServerWebSocket webSocket) {
		new ServerWebSocketHolder(webSocket, handler);
	}
}