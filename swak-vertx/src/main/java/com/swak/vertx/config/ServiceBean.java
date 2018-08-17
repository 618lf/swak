package com.swak.vertx.config;

/**
 * 创建服务 bean
 * 
 * @author lifeng
 */
public class ServiceBean {

	private final Object service;
	private final Class<?> type;

	public ServiceBean(Object service, Class<?> type) {
		this.service = service;
		this.type = type;
	}

	public Object getService() {
		return service;
	}

	public Class<?> getServiceType() {
		return type;
	}
}