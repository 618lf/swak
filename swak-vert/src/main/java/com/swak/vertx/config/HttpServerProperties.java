package com.swak.vertx.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 服务器的配置
 * @author lifeng
 */
@ConfigurationProperties(prefix = "spring.http-server")
public class HttpServerProperties {

	private int port;
	private int maxWebsocketFrameSize = 1000000;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxWebsocketFrameSize() {
		return maxWebsocketFrameSize;
	}

	public void setMaxWebsocketFrameSize(int maxWebsocketFrameSize) {
		this.maxWebsocketFrameSize = maxWebsocketFrameSize;
	}
}
