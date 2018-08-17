package com.swak.vertx.config;

import java.lang.reflect.Proxy;

import com.swak.vertx.handler.InvokerHandler;

import io.vertx.core.Vertx;

/**
 * 依赖配置
 * 
 * @author lifeng
 */
public class ReferenceBean {

	private final Class<?> type;
	private Object refer;

	public ReferenceBean(Class<?> type) {
		this.type = type;
	}

	/**
	 * 获得代理对象,可以切换为其他代理实现
	 * 
	 * @return
	 */
	public Object getRefer(Vertx vertx) {
		if (refer == null) {
			refer = Proxy.newProxyInstance(this.type.getClassLoader(), new Class[] { this.type },
					new InvokerHandler(vertx, type));
		}
		return refer;
	}
}