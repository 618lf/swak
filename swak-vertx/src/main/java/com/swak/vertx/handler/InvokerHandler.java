package com.swak.vertx.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.vertx.core.Vertx;

/**
 * 
 * @author lifeng
 */
public class InvokerHandler implements InvocationHandler {

	private final Vertx vertx;
	private final Class<?> type;

	public InvokerHandler(Vertx vertx, Class<?> type) {
		this.vertx = vertx;
		this.type = type;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("发布一个消息到地址：" + type.getName());
		vertx.eventBus().send(type.getName(), "hello？");
		return null;
	}
}
