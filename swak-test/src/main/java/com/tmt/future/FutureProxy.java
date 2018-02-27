package com.tmt.future;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.util.ClassUtils;

/**
 * 用于返回一个 FutureProxy 代理 代理的是 T， Future get 的也是 T
 * 
 * @author lifeng
 * @param <T>
 */
public abstract class FutureProxy {

	// 线程池
	private static ExecutorService service = Executors.newCachedThreadPool();

	/**
	 * @param target
	 * @return
	 */
	@SuppressWarnings({ "unchecked"})
	public static <T> AsyncData<T> proxy(AsyncData<T> instance) {

		Class<?>[] ins = ClassUtils.getAllInterfaces(instance);

		Class<?> target = null;
		for (Class<?> clazz : ins) {
			if (clazz != Callable.class) {
				target = clazz;
				break;
			}
		}

		Future<AsyncData<T>> future = service.submit(new Callable<AsyncData<T>>() {
			@Override
			public AsyncData<T> call() throws Exception {
				return instance.fetch();
			}
		});

		return (AsyncData<T>)Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[] { target },
				new InvocationHandlerImpl<AsyncData<T>>(future));
	}
}

class InvocationHandlerImpl<T> implements InvocationHandler {

	Future<T> future;

	InvocationHandlerImpl(Future<T> future) {
		this.future = future;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		synchronized (this) {
			T instance = null;
			if (this.future.isDone()) {
				instance = this.future.get();
			} else {
				while (!this.future.isDone()) {
					try {
						instance = this.future.get();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
			return method.invoke(instance, args);
		}
	}
}
