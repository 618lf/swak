package com.swak.vertx;

import io.vertx.core.AbstractVerticle;

public class OrderVerticle extends AbstractVerticle {

	/**
	 * 注册订单服务
	 */
	@Override
	public void start() throws Exception {
		this.vertx.eventBus().consumer("order").handler(new ReceiveHanlder());
	}
}
