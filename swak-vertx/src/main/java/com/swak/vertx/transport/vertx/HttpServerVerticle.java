package com.swak.vertx.transport.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.utils.StringUtils;
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

	protected Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);

	private final Router router;
	private final HttpServerOptions options;
	private final String host;
	private final int port;

	public HttpServerVerticle(Router router, HttpServerOptions httpServerOptions, String host, int port) {
		this.router = router;
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
			vertx.createHttpServer(options).requestHandler(router).exceptionHandler(this::handle).listen(port,
					res -> this.startResult(startPromise, res));
		} else {
			vertx.createHttpServer(options).requestHandler(router).exceptionHandler(this::handle).listen(port, host,
					res -> this.startResult(startPromise, res));
		}
	}

	/**
	 * 处理错误消息
	 * 
	 * @param e 错误
	 */
	public void handle(Throwable e) {
		logger.error("Http Server 错误：", e);
	}
}