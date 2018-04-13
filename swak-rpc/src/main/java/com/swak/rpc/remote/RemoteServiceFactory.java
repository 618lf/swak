package com.swak.rpc.remote;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class RemoteServiceFactory implements Closeable {

	private final InvocationHandler handler = new RemoteHandler();

	@Override
	public void close() throws IOException {

	}

	/**
	 * 将clazz注册为服务
	 * 
	 * @param clazz
	 */
	public Object register(Class<?> clazz) {
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, handler);
	}
}
