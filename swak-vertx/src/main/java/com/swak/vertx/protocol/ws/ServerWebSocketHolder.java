package com.swak.vertx.protocol.ws;

import io.vertx.core.http.ServerWebSocket;

/**
 * 代理 ServerWebSocket 让编程尽量简单
 * 
 * @author lifeng
 * @date 2020年4月25日 下午6:25:03
 */
public class ServerWebSocketHolder {

	private final ServerWebSocket decorate;
	private final WebSocketHandler handler;

	public ServerWebSocketHolder(ServerWebSocket decorate, WebSocketHandler handler) {
		this.decorate = decorate;
		this.handler = handler;
		this.bindEvent();
	}

	// 绑定事件
	private void bindEvent() {

		// 连接
		handler.onConnect(decorate);

		// 处理异常
		decorate.exceptionHandler(e -> {
			handler.onException(decorate, e);
		});

		// 处理消息
		decorate.frameHandler(message -> {
			handler.onMessage(decorate, message);
		});

		// 关闭
		decorate.closeHandler(v -> {
			handler.onClose(decorate);
		});
	}
}
