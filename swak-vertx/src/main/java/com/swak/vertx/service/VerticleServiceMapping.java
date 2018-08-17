package com.swak.vertx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import com.swak.vertx.annotation.ServiceMapping;
import com.swak.vertx.utils.Lifecycle;

/**
 * 将seivice 发布为 verticle
 * 
 * @author lifeng
 */
public class VerticleServiceMapping implements ApplicationContextAware {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.initServiceMappings(applicationContext);
	}
	
	protected void initServiceMappings(ApplicationContext applicationContext) {
		String[] beanNames = applicationContext.getBeanNamesForAnnotation(Service.class);
		for (String beanName : beanNames) {
			Object handler = null;
			try {
				handler = applicationContext.getBean(beanName);
			} catch (Throwable ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
				}
			}
			// 校验 handler
			if (handler == null) {
				throw new IllegalStateException("Invalid mapping on handler class [" + beanName + "]");
			}
			this.registryMapping(handler);
		}
	}
	
	protected void registryMapping(Object handler) {
		final Class<?> userType = ClassUtils.getUserClass(handler.getClass());
		ServiceMapping serviceMapping = AnnotatedElementUtils.findMergedAnnotation(userType, ServiceMapping.class);
		if (serviceMapping == null) {
			return;
		}
		
		// 创建一个 Verticle
		ServiceVerticle verticle = new ServiceVerticle(handler);
		
		// 发布 Verticle
		Lifecycle.vertx.deployVerticle(verticle);
	}
}