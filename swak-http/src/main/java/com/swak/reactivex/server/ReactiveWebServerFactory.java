package com.swak.reactivex.server;

import org.springframework.boot.web.server.WebServer;

import com.swak.reactivex.handler.HttpHandler;

/**
 * 还有一些属性每配置，在研究
 * @author lifeng
 */
public class ReactiveWebServerFactory {

	private HttpServerProperties properties;
	
	public ReactiveWebServerFactory(HttpServerProperties properties) {
		this.properties = properties;
	}
	
	/**
	 * 获得webServer
	 * @param handler
	 * @return
	 */
	public WebServer getWebServer(HttpHandler handler) {
		ReactiveWebServer httpServer = ReactiveWebServer.build(properties);
		return new NettyWebServer(httpServer, handler);
	}
}