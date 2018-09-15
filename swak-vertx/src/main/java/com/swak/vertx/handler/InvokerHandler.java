package com.swak.vertx.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.InvokerAddress;
import com.swak.vertx.transport.codec.Msg;
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

		// 访问的地址
		String address = StringUtils.EMPTY;

		// 定义的访问的地址
		InvokerAddress invokerAddress = type.getAnnotation(InvokerAddress.class);
		if (invokerAddress != null) {
			address = invokerAddress.value();
		}

		// 默认使用接口的全额限定名称
		if (StringUtils.isBlank(address)) {
			address = type.getName();
		}

		// 约定去掉后面的 Async
		return StringUtils.substringBeforeLast(address, "Async");
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
		vertx.sentMessage(this.address, request, meta.getTimeOut(), res -> {
			Msg result = (Msg) res.result().body();
			future.complete(result.getResult());
		});
		return future;
	}
}
