package com.swak.vertx.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import com.swak.utils.StringUtils;
import com.swak.vertx.handler.codec.Msg;
import com.swak.vertx.utils.MethodCache;
import com.swak.vertx.utils.MethodCache.MethodMeta;

/**
 * 调用执行器
 * 
 * @author lifeng
 */
public class InvokerHandler implements InvocationHandler {

	private final Class<?> type;
	private final String address;
	private final VertxHandler vertx;

	public InvokerHandler(VertxHandler vertx, Class<?> type) {
		this.vertx = vertx;
		this.type = type;
		this.address = this.initAddress();
		this.initMethods();
	}
	
	private String initAddress() {
		String name = type.getName();
		return StringUtils.substringBeforeLast(name, "Async");
	}

	private void initMethods() {
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			MethodCache.set(method);
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MethodMeta meta = MethodCache.get(method);
		CompletableFuture<Object> future = new CompletableFuture<Object>();
		Msg request = new Msg(meta, args);
		vertx.sentMessage(this.address, request, res -> {
			Msg result = (Msg) res.result().body();
			future.complete(result.getResult());
		});
		return future;
	}
}
