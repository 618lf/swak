package com.swak.vertx.config;

import java.util.function.Consumer;

import com.swak.exception.BaseRuntimeException;
import com.swak.reactivex.transport.TransportMode;
import com.swak.vertx.handler.VertxHandler;
import com.swak.vertx.handler.codec.Msg;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

/**
 * vertx 的配置 bean
 * 
 * @author lifeng
 */
public class VertxBean implements VertxHandler {

	protected VertxProperties properties;
	protected boolean inited;
	protected Vertx vertx;

	public VertxBean(VertxProperties properties) {
		this.properties = properties;
	}

	/**
	 * 初始化配置项目
	 * 
	 * @return
	 */
	protected VertxOptions init() {
		VertxOptions vertxOptions = new VertxOptions();

		// Dropwizard Metrics
		if (properties.isMetricAble()) {
			vertxOptions.setMetricsOptions(
					new DropwizardMetricsOptions().setEnabled(true).setJmxEnabled(true).setJmxDomain("vertx-metrics"));
		}

		// pool config
		if (properties.getMode() == TransportMode.EPOLL) {
			vertxOptions.setPreferNativeTransport(true);
		}
		vertxOptions.setEventLoopPoolSize(properties.getEventLoopPoolSize());
		return vertxOptions;
	}

	/**
	 * 启动服务器
	 * 
	 * @param startFuture
	 */
	@Override
	public void apply(Consumer<Vertx> apply) {

		// init vertx Options
		VertxOptions vertxOptions = this.init();

		// build standard vertx
		Vertx vertx = Vertx.vertx(vertxOptions);
		apply.accept(vertx);

		// 初始化完成标志
		this.inited = true;
	    this.vertx = vertx;
	}

	/**
	 * 停止服务器
	 * 
	 * @param startFuture
	 */
	@Override
	public void destroy(Consumer<Vertx> apply) {
		if (this.inited) {
			apply.accept(this.vertx);
		}
	}

	/**
	 * 发送消息服务
	 */
	@Override
	public void sentMessage(String address, Msg request) {
		if (!this.inited) {
			throw new BaseRuntimeException("vertx doesn't inited");
		}

		// 发送消息
		vertx.eventBus().send(address, request);
	}

	/**
	 * 发送消息服务
	 */
	@Override
	public void sentMessage(String address, Msg request, Handler<AsyncResult<Message<Msg>>> replyHandler) {
		if (!this.inited) {
			throw new BaseRuntimeException("vertx doesn't inited");
		}

		// 发送消息
		vertx.eventBus().send(address, request, replyHandler);
	}
}