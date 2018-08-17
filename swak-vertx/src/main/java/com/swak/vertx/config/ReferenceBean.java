package com.swak.vertx.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 依赖配置
 * 
 * @author lifeng
 */
public class ReferenceBean implements InvocationHandler {

	private final Class<?> type;
	private Object refer;

	public ReferenceBean(Class<?> type) {
		this.type = type;
	}

	/**
	 * 获得代理对象,可以切换为其他代理实现
	 * @return
	 */
	public Object getRefer() {
		if (refer == null) {
			refer = Proxy.newProxyInstance(this.type.getClassLoader(), new Class[] { this.type }, this);
		}
		return refer;
	}

	/**
	 * 发送消息
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}
}