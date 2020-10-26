package com.swak.vertx.transport.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.utils.StringUtils;
import com.swak.vertx.protocol.im.ImRouter;
import com.swak.vertx.transport.ServerVerticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

/**
 * 统一服务器
 * 
 * @author lifeng
 * @date 2020年10月26日 上午8:51:46
 */
public class UnifyServerVerticle extends AbstractVerticle implements ServerVerticle {

	protected Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);

	private final Router httpRouter;
	private final ImRouter imRouter;
	private final HttpServerOptions options;
	private final String host;
	private final int port;

	public UnifyServerVerticle(Router httpRouter, ImRouter imRouter, HttpServerOptions httpServerOptions, String host,
			int port) {
		this.httpRouter = httpRouter;
		this.imRouter = imRouter;
		this.options = httpServerOptions;
		this.host = host;
		this.port = port;
	}

	/**
	 * 如果发布多个服务，则可以开启多个Worker， 接收请求后可以接入多个Worker
	 */
	@Override
	public void start(Promise<Void> startPromise) {
		
		// 开启WebSocket的支持
		System.setProperty("vertx.disableWebsockets", Boolean.FALSE.toString());

		if (StringUtils.isBlank(host)) {
			vertx.createHttpServer(options).requestHandler(httpRouter).webSocketHandler(imRouter.newHandler())
					.exceptionHandler(this::handle).listen(port, res -> this.startResult(startPromise, res));
		} else {
			vertx.createHttpServer(options).requestHandler(httpRouter).webSocketHandler(imRouter.newHandler())
					.exceptionHandler(this::handle).listen(port, host, res -> this.startResult(startPromise, res));
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
