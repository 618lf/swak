package com.swak.vertx.config;

import com.swak.annotation.Context;
import com.swak.annotation.FluxService;

import lombok.EqualsAndHashCode;

/**
 * 创建服务 bean
 *
 * @author: lifeng
 * @date: 2020/3/29 18:52
 */
@EqualsAndHashCode
public class ServiceBean implements AbstractConfig {

	private Object ref;
	private Class<?> type;
	private FluxService mapping;

	public Context getContext() {
		return mapping.context();
	}

	public int getInstances() {
		return mapping.instances();
	}

	public String getUse_pool() {
		return mapping.use_pool();
	}

	public Object getRef() {
		return ref;
	}

	public void setRef(Object ref) {
		this.ref = ref;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public FluxService getMapping() {
		return mapping;
	}

	public void setMapping(FluxService mapping) {
		this.mapping = mapping;
	}
}