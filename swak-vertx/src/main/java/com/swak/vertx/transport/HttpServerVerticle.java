package com.swak.vertx.transport;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * http服务器
 * 
 * @author lifeng
 */
public class HttpServerVerticle extends AbstractVerticle {

	private Vertx vertx;
	private Router router;
	private int port;

	public HttpServerVerticle(Vertx vertx, Router router, int port) {
		this.vertx = vertx;
		this.port = port;
		this.router = router;
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		vertx.createHttpServer().requestHandler(router::accept).listen(port, res ->{
			startFuture.complete();
		});
	}
}