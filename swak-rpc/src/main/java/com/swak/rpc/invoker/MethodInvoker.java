package com.swak.rpc.invoker;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * 基于 method 反射执行
 * @author lifeng
 * @param <T>
 */
public class MethodInvoker<T> implements Invoker<T> {

	private final Object service;
	final Class<?> clazz;
	final Method method;
	private final Class<?>[] parameterTypes;
	private final String[] parameterNames;
	private final int parameterCount;
	
	public MethodInvoker(Class<?> clazz, Object service, Method method) {
		this.service = service;
		this.clazz = clazz;
		this.method = method;
		this.parameterTypes = method.getParameterTypes();
		this.parameterCount = parameterTypes.length;
		
		parameterNames = new String[parameterCount];
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameterCount; i++) {
			Parameter parameter = parameters[i];
			parameterNames[i] = parameter.getName();
		}
	}

	@Override
	public T invoke(Object... params) throws InvokeException{
		if (params == null) {
			if (parameterCount != 0) {
				throw new InvokeException(method.getName() + " params count error, params is null");
			}
		} else if (parameterCount != params.length) {
			throw new InvokeException(method.getName() + " params count error, " + Arrays.toString(params));
		}
		return this.invokeMethod(params);
	}
	
	@SuppressWarnings("unchecked")
	private T invokeMethod(Object... params) {
		try {
			return (T) method.invoke(service, params);
		} catch (Exception e) {
			throw new InvokeException("invoke method" + method.getName() + " error" + e.getMessage());
		} 
	}
}