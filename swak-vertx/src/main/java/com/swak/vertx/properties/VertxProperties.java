package com.swak.vertx.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Vertx 的属性配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = "spring.vertx")
public class VertxProperties {

	/**
	 * 默认的端口
	 */
	private int port = 8080;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}