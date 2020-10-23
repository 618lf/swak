package com.swak.vertx.protocol.im.socket;

import java.util.concurrent.CompletableFuture;

import com.swak.vertx.protocol.im.ImSocket;

import io.vertx.core.http.ServerWebSocket;

/**
 * 目前支持异步发送文本消息
 * 
 * @author lifeng
 * @date 2020年10月23日 上午11:34:56
 */
public class ImSocketImpl implements ImSocket {

	ServerWebSocket socket;

	public ImSocketImpl(ServerWebSocket socket) {
		this.socket = socket;
	}

	@Override
	public String remoteAddress() {
		return socket.remoteAddress().host();
	}

	@Override
	public String localAddress() {
		return socket.localAddress().host();
	}

	@Override
	public void sendText(String text) {
		socket.writeTextMessage(text);
	}

	@Override
	public CompletableFuture<Void> sendTextAsync(String text) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		socket.writeTextMessage(text, (a) -> {
			if (a != null && a.cause() != null) {
				future.completeExceptionally(a.cause());
			} else {
				future.complete(null);
			}
		});
		return future;
	}
}
