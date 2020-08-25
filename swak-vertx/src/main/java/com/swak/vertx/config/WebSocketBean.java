package com.swak.vertx.config;

import org.springframework.beans.factory.InitializingBean;

import com.swak.vertx.protocol.ws.WebSocketContext;

import io.vertx.core.Handler;

/**
 * 如何处理WebSocket消息
 * 
 * @author lifeng
 * @date 2020年8月25日 下午2:36:24
 */
public class WebSocketBean implements Handler<WebSocketContext>, InitializingBean, AbstractConfig {

	private int port;

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public void handle(WebSocketContext event) {

	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}