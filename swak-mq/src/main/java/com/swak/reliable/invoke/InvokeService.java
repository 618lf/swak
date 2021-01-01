package com.swak.reliable.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.swak.App;
import com.swak.reliable.ReliableMessageException;
import com.swak.utils.Maps;

/**
 * 调用服务
 * 
 * @author DELL
 */
public class InvokeService {

	private Map<Method, Invoker> invokers = Maps.newConcurrentMap();

	public Object invoke(Class<?> fac, Method invokeMethod, Object[] params)
			throws NoSuchMethodException, InvocationTargetException {
		return this.buildInvoker(fac, invokeMethod).invoke(params);
	}

	private Invoker buildInvoker(Class<?> fac, Method invokeMethod) throws NoSuchMethodException, SecurityException {
		if (!invokers.containsKey(invokeMethod)) {

			// 获得实例
			Object service = App.getBean(fac);
			if (service == null) {
				throw new ReliableMessageException("Fac Need Register In Spring, Use @Service ed.");
			}

			// 获得方法
			Method method = fac.getMethod(invokeMethod.getName(), invokeMethod.getParameterTypes());
			if (method == null) {
				throw new ReliableMessageException("Fac Need Register In Spring, Use @Service ed.");
			}

			return invokers.computeIfAbsent(invokeMethod, (key) -> {
				return new Invoker(fac, service, method);
			});
		}
		return invokers.get(invokeMethod);
	}
}
