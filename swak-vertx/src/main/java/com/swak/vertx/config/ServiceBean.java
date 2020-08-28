package com.swak.vertx.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.swak.annotation.Context;
import com.swak.annotation.FluxService;

import lombok.EqualsAndHashCode;

/**
 * 创建服务 bean
 *
 * @author: lifeng
 * @date: 2020/3/29 18:52
 */
@EqualsAndHashCode(callSuper = false)
public class ServiceBean extends AbstractBean implements InitializingBean {

	private Object ref;
	private Class<?> beanClass;
	private Class<?> interClass;
	private Context context;
	private int instances;
	private String pool;

	@Override
	public void afterPropertiesSet() throws Exception {
		FluxService mapping = AnnotatedElementUtils.findMergedAnnotation(beanClass, FluxService.class);
		this.context = mapping.context();
		this.instances = mapping.instances();
		this.pool = mapping.pool();
	}

	public Object getRef() {
		return ref;
	}

	public void setRef(Object ref) {
		this.ref = ref;
	}

	public Context getContext() {
		return context;
	}

	public int getInstances() {
		return instances;
	}

	public String getPool() {
		return pool;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public Class<?> getInterClass() {
		return interClass;
	}

	public void setInterClass(Class<?> interClass) {
		this.interClass = interClass;
	}
}