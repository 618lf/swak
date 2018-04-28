package com.swak.reactivex.server;

import org.springframework.boot.web.server.WebServer;

import com.swak.reactivex.handler.HttpHandler;

public class ReactiveWebServerFactory {

	private HttpServerProperties properties;
	public ReactiveWebServerFactory(HttpServerProperties properties) {
		this.properties = properties;
	}
	
	public WebServer getWebServer(HttpHandler handler) {
		return new HttpServer(properties, handler);
	}
}