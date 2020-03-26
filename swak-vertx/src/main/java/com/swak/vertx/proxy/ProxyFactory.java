package com.swak.vertx.proxy;

import java.lang.reflect.Proxy;

import com.swak.vertx.transport.VertxProxy;

import net.sf.cglib.proxy.Enhancer;

/**
 * 代理工厂
 * 
 * @author lifeng
 */
public class ProxyFactory {

	/**
	 * 创建代理类： 默认情况下使用 JDK 代理类
	 * 
	 * @param vertx
	 * @param type
	 * @return
	 */
	public static Object newProxy(VertxProxy vertx, Class<?> type) {
		if (type.isInterface()) {
			return newJdkProxy(vertx, type);
		}
		return newCglibProxy(vertx, type);
	}

	/**
	 * 创建 cglib 的代理类：可以支持 接口和类
	 * 
	 * @param vertx
	 * @param type
	 * @return
	 */
	private static Object newCglibProxy(VertxProxy vertx, Class<?> type) {

		// 代理执行类
		FluxInvokerProxy invoker = new FluxInvokerProxy(vertx, type);

		// 创建代理
		Enhancer enhancer = new Enhancer();
		enhancer.setCallback(invoker);
		enhancer.setClassLoader(type.getClassLoader());
		if (type.isInterface()) {
			enhancer.setInterfaces(new Class<?>[] { type });
		} else {
			enhancer.setSuperclass(type);
		}

		// 创建代理类
		return enhancer.create();
	}

	/**
	 * 支持接口
	 * 
	 * @param vertx
	 * @param type
	 * @return
	 */
	private static Object newJdkProxy(VertxProxy vertx, Class<?> type) {
		return Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, new FluxInvokerProxy(vertx, type));
	}
}
