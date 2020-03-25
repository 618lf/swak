package com.swak.vertx.config;

import com.swak.annotation.Context;
import com.swak.annotation.FluxService;
import com.swak.annotation.Server;

/**
 * 创建服务 bean
 * 
 * @author lifeng
 */
public class ServiceBean {

	private final Object service;
	private final Class<?> type;
	private final FluxService mapping;

	public ServiceBean(Class<?> type, Object service, FluxService mapping) {
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