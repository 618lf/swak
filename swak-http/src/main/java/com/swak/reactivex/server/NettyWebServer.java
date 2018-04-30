package com.swak.reactivex.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;

import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.server.tcp.BlockingNettyContext;

/**
 * ReactiveWebServer 的封装
 * @author lifeng
 */
public class NettyWebServer implements WebServer{

	private static final Logger logger = LoggerFactory.getLogger(NettyWebServer.class);
	
	private BlockingNettyContext nettyContext;
	private final HttpHandler httpHandler;
	private final ReactiveWebServer httpServer;
	public NettyWebServer(ReactiveWebServer httpServer, HttpHandler httpHandler) {
		this.httpHandler = httpHandler;
		this.httpServer = httpServer;
	}
	
	@Override
	public void start() throws WebServerException {
		if (this.nettyContext == null) {
			try {
				this.nettyContext = startHttpServer();
			}catch (Exception ex) {
				throw new WebServerException("Unable to start Netty", ex);
			}
		}
		NettyWebServer.logger.info("Netty started on port(s): " + getPort());
		startDaemonAwaitThread(this.nettyContext);
	}
	
	private BlockingNettyContext startHttpServer() {
		return this.httpServer.start(this.httpHandler);
	}
	
	private void startDaemonAwaitThread(BlockingNettyContext nettyContext) {
		Thread awaitThread = new Thread("server") {
			@Override
			public void run() {
				nettyContext.getContext().onClose().blockingSingle();
			}

		};
		awaitThread.setContextClassLoader(getClass().getClassLoader());
		awaitThread.setDaemon(false);
		awaitThread.start();
	}

	@Override
	public void stop() throws WebServerException {
		if (this.nettyContext != null) {
			this.nettyContext.shutdown();
			// temporary fix for gh-9146
			this.nettyContext.getContext().onClose().doOnComplete(()->{}).blockingSingle();
			this.nettyContext = null;
		}
	}

	@Override
	public int getPort() {
		if (this.nettyContext != null) {
			return this.nettyContext.getPort();
		}
		return 0;
	}
}