package com.swak.reactivex.context;

import org.springframework.boot.web.server.WebServerException;

import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.transport.http.server.HttpServer;
import com.swak.reactivex.transport.http.server.HttpServerProperties;

/**
 * 响应式的 http 服务器
 * 
 * @author lifeng
 */
public class ReactiveWebServer extends HttpServer implements WebServer {
	
	private ReactiveWebServer(HttpServerProperties properties) {
		super(properties);
	}

	@Override
	public void start(HttpHandler handler) throws WebServerException {
		super.start(handler);
	}
	
	@Override
	public int getPort() {
		return getAddress().getPort();
	}

	/**
	 * 创建 http 服务器
	 * 
	 * @param options
	 * @return
	 */
	public static ReactiveWebServer build(HttpServerProperties properties) {
		return new ReactiveWebServer(properties);
	}
}