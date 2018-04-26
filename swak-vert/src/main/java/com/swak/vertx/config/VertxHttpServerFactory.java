package com.swak.vertx.config;

import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;

/**
 * 基于
 * 
 * @author lifeng
 */
public class VertxHttpServerFactory extends AbstractVerticle {

	private Router router;
	private HttpServer server;
	private HttpServerProperties config;

	public VertxHttpServerFactory(HttpServerProperties config) {
		this.config = config;
	}

	/**
	 * 启动服务
	 */
	public void start() {
		VertxFactory.V.deployVerticle(this);
	}

	/**
	 * 重写启动verticle
	 * 
	 * @method start
	 * @author Neil.Zhou
	 * @param future
	 * @return void
	 * @exception @date
	 *                2017/9/21 0:33
	 */
	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		HttpServerOptions options = new HttpServerOptions().setMaxWebsocketFrameSize(config.getMaxWebsocketFrameSize())
				.setPort(config.getPort());
		server = vertx.createHttpServer(options);
		server.requestHandler(router::accept);
		server.listen(result -> {
			if (result.succeeded()) {
				
				/**
				 * 初始化路由
				 */
				initRouter();
				
				future.complete();
			} else {
				future.fail(result.cause());
			}
		});
	}
	
	/**
	 * 初始化路由
	 */
	private void initRouter() {
		Router router = VertxFactory.R;
		router.route().handler(ctx -> {
			ctx.response().headers().add(CONTENT_TYPE, "application/json; charset=utf-8");
			ctx.response().headers().add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			ctx.response().headers().add(ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, PUT, DELETE, HEAD");
			ctx.response().headers().add(ACCESS_CONTROL_ALLOW_HEADERS,
					"X-PINGOTHER, Origin,Content-Type, Accept, X-Requested-With,Dev,Authorization,Version,orgCode");
			ctx.response().headers().add(ACCESS_CONTROL_MAX_AGE, "1728000");
			ctx.next();
		});
		router.route().handler(CookieHandler.create());
	    router.route().handler(BodyHandler.create());
	}

	/**
	 * 重写停止verticle
	 * 
	 * @method start
	 * @author Neil.Zhou
	 * @param future
	 * @return void
	 * @exception @date
	 *                2017/9/21 0:33
	 */
	@Override
	public void stop(Future<Void> future) {
		if (server == null) {
			future.complete();
			return;
		}
		server.close(result -> {
			if (result.failed()) {
				future.fail(result.cause());
			} else {
				future.complete();
			}
		});
	}
}