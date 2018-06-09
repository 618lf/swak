package com.swak.rpc.invoker;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * 用于创建 Invoker
 * @author lifeng
 */
public class InvokerFactory {

	/**
	 * 构建一个执行器
	 * @param handler
	 * @param method
	 * @return
	 */
	public static <T> Invoker<CompletableFuture<T>> build(Class<?> classType, Object handler, Method method) {
		return new MethodInvoker<CompletableFuture<T>>(classType, handler, method);
	}
}