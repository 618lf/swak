package com.swak.vertx.config;

import com.swak.vertx.proxy.ProxyFactory;
import com.swak.vertx.transport.VertxProxy;

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
	public Object getRefer(VertxProxy vertx) {
		if (refer == null) {
			refer = ProxyFactory.newProxy(vertx, type);
		}
		return refer;
	}
}