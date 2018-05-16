package com.swak.reactivex.server;

import org.springframework.boot.web.server.WebServerException;

import com.swak.reactivex.handler.HttpHandler;

/**
 * ReactiveWebServer 的封装
 * @author lifeng
 */
public class NettyWebServer implements WebServer{
	
	private final ReactiveWebServer httpServer;
	
	public NettyWebServer(ReactiveWebServer httpServer) {
		this.httpServer = httpServer;
	}
	
	@Override
	public void start(HttpHandler httpHandler) throws WebServerException {
		try {
			this.httpServer.start(httpHandler);
		}catch (Exception ex) {
			throw new WebServerException("Unable to start Netty", ex);
		}
	}

	@Override
	public void stop() throws WebServerException {
		this.httpServer.stop();
	}

	@Override
	public int getPort() {
		return this.httpServer.getAddress().getPort();
	}
}