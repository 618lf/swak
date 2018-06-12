package com.swak.rpc.invoker;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

import com.swak.rpc.api.Constants;
import com.swak.rpc.api.URL;
import com.swak.utils.Maps;

/**
 * 基于 method 反射执行
 * @author lifeng
 * @param <T>
 */
public class MethodInvoker<T> implements Invoker<T> {

	private final Invocation invocation;
	private final String[] parameterNames;
	private final int parameterCount;
	
	public MethodInvoker(Invocation invocation) {
		this.invocation = invocation;
		this.parameterCount = invocation.getParameterTypes().length;
		parameterNames = new String[parameterCount];
		Parameter[] parameters = invocation.getMethod().getParameters();
		for (int i = 0; i < parameterCount; i++) {
			Parameter parameter = parameters[i];
			parameterNames[i] = parameter.getName();
		}
	}

	@Override
	public T invoke(Object... params) throws InvokeException{
		if (params == null) {
			if (parameterCount != 0) {
				throw new InvokeException(invocation.getMethod().getName() + " params count error, params is null");
			}
		} else if (parameterCount != params.length) {
			throw new InvokeException(invocation.getMethod().getName() + " params count error, " + Arrays.toString(params));
		}
		return this.invokeMethod(invocation.getService(), invocation.getMethod(), params);
	}
	
	@SuppressWarnings("unchecked")
	private T invokeMethod(Object service, Method method, Object... params) {
		try {
			return (T) method.invoke(service, params);
		} catch (Exception e) {
			throw new InvokeException("invoke method" + method.getName() + " error" + e.getMessage());
		} 
	}

	/**
	 * 一般地址
	 */
	@Override
	public URL getURL() {
		Map<String, String> parameters  = Maps.newHashMap();
		parameters.put(Constants.VERSION_KEY, invocation.getVersion());
		parameters.put(Constants.GROUP_KEY, invocation.getGroup());
		parameters.put(Constants.METHOD_KEY, invocation.getMethod().getName());
		for(String m : parameterNames) {
			parameters.put(Constants.METHOD_PARAM_KEY_PREFIX + m, m);
		}
		return new URL(null, null, 0, invocation.getServiceType().getName(), parameters);
	}
}