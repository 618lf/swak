package com.swak.vertx.transport.vertx;

import com.swak.vertx.config.ServiceBean;
import com.swak.vertx.transport.codec.Msg;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * 服务 Verticle
 *
 * @author: lifeng
 * @date: 2020/3/29 21:20
 */
public class ServiceVerticle extends AbstractVerticle {

	private final ServiceBean service;
	private final String address;

	public ServiceVerticle(ServiceBean service) {
		this.service = service;
		this.address = service.getInterClass().getName();
	}

	/**
	 * 如果发布多个服务，则轮询使用多个服务 <br>
	 * @ConcurrentCyclicSequence.next() 逻辑部分
	 */
	@Override
	public void start(Promise<Void> startPromise) {
		this.getVertx().eventBus().<Msg>consumer(address).handler(service);
		startPromise.complete();
	}

	/**
	 * 定义停止
	 */
	@Override
	public void stop() {
		this.getVertx().eventBus().consumer(address).unregister();
	}
}