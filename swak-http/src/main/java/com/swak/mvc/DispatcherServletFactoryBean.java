package com.swak.mvc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 创建servlet
 * 
 * @author lifeng
 */
public class DispatcherServletFactoryBean implements FactoryBean<DispatcherServlet>, ApplicationContextAware {

	private ApplicationContext applicationContext;
	private DispatcherServlet instance;

	@Override
	public DispatcherServlet getObject() throws Exception {
		if (instance == null) {
			instance = createInstance();
		}
		return instance;
	}

	@Override
	public Class<?> getObjectType() {
		return DispatcherServlet.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	private DispatcherServlet createInstance() {
		instance = new DispatcherServlet();
		instance.init(applicationContext);
		return instance;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}