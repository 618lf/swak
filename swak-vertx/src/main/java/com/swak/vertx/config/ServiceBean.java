package com.swak.vertx.config;

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

	public boolean isHttp() {
		return mapping.http();
	}

	public int getInstances() {
		return mapping.instances();
	}

	public String getUse_pool() {
		return mapping.use_pool();
	}

	@Deprecated
	public boolean isAop() {
		return mapping.isAop();
	}
}