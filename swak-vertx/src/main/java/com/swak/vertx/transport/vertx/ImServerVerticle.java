package com.swak.vertx.transport.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.utils.StringUtils;
import com.swak.vertx.protocol.im.ImRouter;
import com.swak.vertx.transport.ServerVerticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;

/**
 * WebSocket服务
 * 
 * @author lifeng
 * @date 2020年4月23日 下午3:01:23
 */
public class ImServerVerticle extends AbstractVerticle implements ServerVerticle {

	protected Logger logger = LoggerFactory.getLogger(ImServerVerticle.class);

	private final ImRouter imRouter;
	private final HttpServerOptions options;
	private final String host;
	private final int port;

	public ImServerVerticle(ImRouter imRouter, HttpServerOptions options, String host, int port) {
		this.imRouter = imRouter;
		this.options = options;
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
			vertx.createHttpServer(options).webSocketHandler(imRouter.newHandler()).exceptionHandler(this::handle)
					.listen(port, res -> this.startResult(startPromise, res));
		} else {
			vertx.createHttpServer(options).webSocketHandler(imRouter.newHandler()).exceptionHandler(this::handle)
					.listen(port, host, res -> this.startResult(startPromise, res));
		}
	}

	/**
	 * 处理错误消息
	 * 
	 * @param e 错误
	 */
	public void handle(Throwable e) {
		logger.error("Web Socket 错误：", e);
	}
}