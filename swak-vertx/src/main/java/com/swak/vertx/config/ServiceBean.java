package com.swak.vertx.config;

/**
 * 创建服务 bean
 * 
 * @author lifeng
 */
public class ServiceBean {

	private final Object service;
	private final boolean isHttpServer;
	private final int instances;
	private final String use_pool;
	private final Class<?> type;

	public ServiceBean(Object service, boolean isHttpServer, int instances, String use_pool, Class<?> type) {
		this.service = service;
		this.isHttpServer = isHttpServer;
		this.instances = instances;
		this.use_pool = use_pool;
		this.type = type;
	}

	public boolean isHttpServer() {
		return isHttpServer;
	}
	public int getInstances() {
		return instances;
	}
	public Class<?> getType() {
		return type;
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