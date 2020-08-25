package com.swak.vertx.transport.vertx;

import com.swak.utils.StringUtils;
import com.swak.vertx.protocol.ws.ServerWebSocketHolder;
import com.swak.vertx.protocol.ws.WebSocketHandler;
import com.swak.vertx.transport.ServerVerticle;

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
public class WebSockerServerVerticle extends AbstractVerticle implements Handler<ServerWebSocket>, ServerVerticle {

	private final WebSocketHandler handler;
	private final HttpServerOptions options;
	private final String host;
	private final int port;

	public WebSockerServerVerticle(WebSocketHandler handler, HttpServerOptions options, String host, int port) {
		this.options = options;
		this.handler = handler;
		this.host = host;
		this.port = port;
	}

	@Override
	public void start(Promise<Void> startPromise) {

		// 发布服务
		if (StringUtils.isBlank(host)) {
			vertx.createHttpServer(options).webSocketHandler(this).listen(port,
					res -> this.startResult(startPromise, res));
		} else {
			vertx.createHttpServer(options).webSocketHandler(this).listen(port, host,
					res -> this.startResult(startPromise, res));
		}
	}

	@Override
	public void handle(ServerWebSocket webSocket) {
		new ServerWebSocketHolder(webSocket, handler);
	}
}