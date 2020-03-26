package com.swak.vertx.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.swak.asm.MethodCache;
import com.swak.vertx.transport.VertxProxy;

/**
 * 调用执行器
 * 
 * @author lifeng
 */
public class FluxInvokerProxy implements InvocationHandler, FluxInvoker {

	private final Class<?> type;
	private final String address;
	private final VertxProxy vertx;

	public FluxInvokerProxy(VertxProxy vertx, Class<?> type) {
		this.vertx = vertx;
		this.type = type;
		this.address = this.getAddress(type);
		this.initMethods();
	}

	/**
	 * 缓存方法
	 */
	private void initMethods() {
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			MethodCache.set(method);
		}
	}

	/**
	 * 只支持异步接口的调用
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return this.invoke(vertx, address, method, args);
	}
}
