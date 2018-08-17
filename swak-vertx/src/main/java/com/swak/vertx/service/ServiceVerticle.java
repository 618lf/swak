package com.swak.vertx.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

/**
 * 自动创建这样一个 Verticle
 * 
 * @author lifeng
 */
public class ServiceVerticle extends AbstractVerticle implements Handler<Message<String>> {

	private final Object service;
	private final String address;

	public ServiceVerticle(Object service) {
		this.service = service;
		this.address = service.getClass().getName();
	}
	
    /**
     * 注册为消费者
     */
    @Override
    public void start() throws Exception {
        super.start();
        this.getVertx().eventBus().<String>consumer(address).handler(this);
    }

    /**
     * 处理消息
     */
	@Override
	public void handle(Message<String> event) {
		
	}
}