package com.swak.vertx.transport;

import com.swak.utils.StringUtils;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.protocol.http.RouterHandler;

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

	private final VertxProperties properties;
	private final RouterHandler routerHandler;

	public HttpServerVerticle(RouterHandler routerHandler, VertxProperties properties) {
		this.routerHandler = routerHandler;
		this.properties = properties;
	}

	/**
	 * startFuture.complete 底层也没有修改，暂时不知道修改方案
	 */
	@Override
	public void start(Promise<Void> startPromise) {

		// 获得路由 -- Router 是线程安全的所以多个Verticle实例可以公用
		Router router = routerHandler.getRouter();

		// 服务器配置
		HttpServerOptions options = this.serverOptions(properties);

		// 发布服务
		if (StringUtils.isBlank(properties.getHost())) {
			vertx.createHttpServer(options).requestHandler(router).listen(properties.getPort(),
					res -> this.startResult(startPromise, res));
		} else {
			vertx.createHttpServer(options).requestHandler(router).listen(properties.getPort(), properties.getHost(),
					res -> this.startResult(startPromise, res));
		}
	}
}