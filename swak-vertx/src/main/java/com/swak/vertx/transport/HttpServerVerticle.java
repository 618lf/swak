package com.swak.vertx.transport;

import com.swak.utils.StringUtils;
import com.swak.vertx.handler.RouterHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

/**
 * http服务器
 *
 * @author: lifeng
 * @date: 2020/3/29 21:15
 */
public class HttpServerVerticle extends AbstractVerticle {

	private String host;
	private int port;
	private final RouterHandler routerHandler;

	public HttpServerVerticle(RouterHandler routerHandler, String host, int port) {
		this.routerHandler = routerHandler;
		this.host = host;
		this.port = port;
	}

	/**
	 * startFuture.complete 底层也没有修改，暂时不知道修改方案
	 */
	@Override
	public void start(Promise<Void> startPromise) {

		// 获得路由 -- Router 是线程安全的所以多个Verticle实例可以公用
		Router router = routerHandler.getRouter();

		// 发布服务
		if (StringUtils.isBlank(host)) {
			vertx.createHttpServer().requestHandler(router).listen(port, res -> startPromise.complete());
		} else {
			vertx.createHttpServer().requestHandler(router).listen(port, host, res -> startPromise.complete());
		}
	}
}