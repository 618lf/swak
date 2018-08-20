package com.swak.vertx.config;

/**
 * 创建服务 bean
 * 
 * @author lifeng
 */
public class ServiceBean {

	private final Object service;
	private final String use_pool;
	private final Class<?> type;

	public ServiceBean(Object service, String use_pool, Class<?> type) {
		this.service = service;
		this.use_pool = use_pool;
		this.type = type;
	}

	public String getUse_pool() {
		return use_pool;
	}

	public Object getService() {
		return service;
	}

	public Class<?> getServiceType() {
		return type;
	}
}