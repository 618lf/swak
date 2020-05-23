package com.swak.vertx.protocol.ws;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

/**
 * WebSocket 的处理器
 * 
 * @author lifeng
 * @date 2020年4月25日 下午6:02:22
 */
public interface WebSocketHandler {

	/**
	 * 连接
	 */
	void onConnect(ServerWebSocket socket);

	/**
	 * 处理消息
	 * 
	 * @param message 消息
	 */
	void onMessage(ServerWebSocket socket, WebSocketFrame message);

	/**
	 * 异常
	 * 
	 * @param socket 连接
	 * @param e      异常
	 */
	void onException(ServerWebSocket socket, Throwable e);

	/**
	 * 关闭
	 */
	void onClose(ServerWebSocket socket);

}