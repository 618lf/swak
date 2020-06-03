package com.sample.tools.plugin.config;

import org.springframework.beans.factory.FactoryBean;

public class PluginFactoryBean<T> implements FactoryBean<T> {
	private Class<T> mapperInterface;

	public PluginFactoryBean() {
	}

	public PluginFactoryBean(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	public void setMapperInterface(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	@Override
	public T getObject() throws Exception {
		return mapperInterface.newInstance();
	}

	@Override
	public Class<?> getObjectType() {
		return mapperInterface;
	}
}
