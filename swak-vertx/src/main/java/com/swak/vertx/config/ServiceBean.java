package com.swak.vertx.config;

import com.swak.annotation.Context;
import com.swak.annotation.FluxService;
import com.swak.annotation.Server;

import lombok.EqualsAndHashCode;

/**
 * 创建服务 bean
 *
 * @author: lifeng
 * @date: 2020/3/29 18:52
 */
@EqualsAndHashCode
public class ServiceBean {

	private Object service;
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

	public void setService(Object service) {
		this.service = service;
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