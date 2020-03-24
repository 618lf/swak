package com.swak.vertx.config;

import com.swak.vertx.annotation.Context;
import com.swak.vertx.annotation.Server;
import com.swak.vertx.annotation.VertxService;

/**
 * 创建服务 bean
 * 
 * @author lifeng
 */
public class ServiceBean {

	private final Object service;
	private final Class<?> type;
	private final VertxService mapping;

	public ServiceBean(Class<?> type, Object service, VertxService mapping) {
		this.service = service;
		this.mapping = mapping;
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}

	public Object getService() {
		return service;
	}

	public Class<?> getServiceType() {
		return type;
	}

	public Server getServer() {
		return mapping.server();
	}

	public Context getContext() {
		return mapping.context();
	}

	public int getInstances() {
		return mapping.instances();
	}

	public String getUse_pool() {
		return mapping.use_pool();
	}
}