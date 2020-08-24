package com.swak.vertx.config;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.vertx.proxy.ProxyFactory;
import com.swak.vertx.transport.VertxProxy;

/**
 * 依赖配置
 *
 * @author: lifeng
 * @date: 2020/3/29 19:10
 */
public class ReferenceBean {

	@Autowired
	private VertxProxy vertx;
	private Class<?> type;
	private Object refer;

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Object getRefer() {
		return refer;
	}

	public void setRefer(Object refer) {
		this.refer = refer;
	}

	/**
	 * 获得代理对象,可以切换为其他代理实现
	 *
	 * @param vertx vertx代理
	 * @return 代理对象
	 */
	public Object get() {
		if (refer == null) {
			refer = ProxyFactory.newProxy(vertx, type);
		}
		return refer;
	}
}