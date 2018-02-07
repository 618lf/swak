package com.swak.schema;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.swak.http.Servlet;

/**
 * 初始化 Servlet
 * 
 * @author lifeng
 */
public class ServletBean implements FactoryBean<Servlet>, ApplicationContextAware, InitializingBean, DisposableBean {

	private transient volatile boolean initialized;
	private ApplicationContext applicationContext;
	private Servlet servlet;
	private String target;

	@Override
	public void destroy() throws Exception {
       this.applicationContext = null;
       this.target = null;
       this.servlet.destroy();
	}

	@Override
	public Servlet getObject() throws Exception {
		if (servlet == null) {
		    this.init();
		}
		return servlet;
	}

	@Override
	public Class<?> getObjectType() {
		return Servlet.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.init();
	}

	// 初始化并启动服务
	private void init() throws Exception {
		if (initialized) {
			return;
		}
		initialized = true;
		servlet = (Servlet) Class.forName(target).newInstance();
		
		// 初始化
		servlet.init(applicationContext);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}