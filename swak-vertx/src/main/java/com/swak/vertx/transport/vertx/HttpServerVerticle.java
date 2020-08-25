package com.swak.vertx.transport.vertx;

import com.swak.utils.StringUtils;
import com.swak.vertx.protocol.http.RouterHandler;
import com.swak.vertx.transport.ServerVerticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

/**
 * http服务器
 *
 * @author: lifeng
 * @date: 2020/3/29 21:15
 */
public class HttpServerVerticle extends AbstractVerticle implements ServerVerticle {

	private final Router router;
	private final RouterHandler routerHandler;
	private final HttpServerOptions options;
	private final String host;
	private final int port;

	public HttpServerVerticle(Router router, RouterHandler routerHandler, HttpServerOptions httpServerOptions,
			String host, int port) {
		this.router = router;
		this.routerHandler = routerHandler;
		this.options = httpServerOptions;
		this.host = host;
		this.port = port;
	}

	/**
	 * startFuture.complete 底层也没有修改，暂时不知道修改方案
	 */
	@Override
	public void start(Promise<Void> startPromise) {

		// 发布服务
		if (StringUtils.isBlank(host)) {
			vertx.createHttpServer(options).requestHandler(router).exceptionHandler(routerHandler::handle).listen(port,
					res -> this.startResult(startPromise, res));
		} else {
			vertx.createHttpServer(options).requestHandler(router).exceptionHandler(routerHandler::handle).listen(port,
					host, res -> this.startResult(startPromise, res));
		}
	}
}