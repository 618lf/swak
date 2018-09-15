package com.swak.vertx.config;

import java.util.function.Consumer;

import com.swak.exception.BaseRuntimeException;
import com.swak.vertx.handler.VertxHandler;
import com.swak.vertx.transport.codec.Msg;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

/**
 * vertx 的配置 bean
 * 
 * @author lifeng
 */
public class VertxBean implements VertxHandler {

	protected VertxOptions vertxOptions;
	protected DeliveryOptions deliveryOptions;
	protected boolean inited;
	protected Vertx vertx;

	public VertxBean(VertxOptions vertxOptions, DeliveryOptions deliveryOptions) {
		this.vertxOptions = vertxOptions;
		this.deliveryOptions = deliveryOptions;
	}

	/**
	 * 启动服务器
	 * 
	 * @param startFuture
	 */
	@Override
	public void apply(Consumer<Vertx> apply) {
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
	public void sentMessage(String address, Msg request, int timeout) {
		if (!this.inited) {
			throw new BaseRuntimeException("vertx doesn't inited");
		}

		// 默认的配置
		DeliveryOptions deliveryOptions = this.deliveryOptions;
		if (timeout >= 1) {
			deliveryOptions = new DeliveryOptions();
			deliveryOptions.setSendTimeout(timeout);
		}

		// 发送消息
		vertx.eventBus().send(address, request, deliveryOptions);
	}

	/**
	 * 发送消息服务
	 */
	@Override
	public void sentMessage(String address, Msg request, int timeout, Handler<AsyncResult<Message<Msg>>> replyHandler) {
		if (!this.inited) {
			throw new BaseRuntimeException("vertx doesn't inited");
		}
		
		// 默认的配置
		DeliveryOptions deliveryOptions = this.deliveryOptions;
		if (timeout >= 1) {
			deliveryOptions = new DeliveryOptions();
			deliveryOptions.setSendTimeout(timeout);
		}

		// 发送消息
		vertx.eventBus().send(address, request, deliveryOptions, replyHandler);
	}
}