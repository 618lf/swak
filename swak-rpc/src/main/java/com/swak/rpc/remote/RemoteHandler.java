package com.swak.rpc.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 接口拦截器
 * @author lifeng
 */
public class RemoteHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("invoke service: " + method.getDeclaringClass().getName());
		return null;
	}
}