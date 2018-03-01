package com.swak.http;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.swak.http.Server;
import com.swak.http.metric.MetricCenter;
import com.swak.http.server.HttpServer;

/**
 * 服务
 * 
 * @author lifeng
 */
public class ServerFactoryBean extends HttpServer.Builder implements FactoryBean<Server>, ApplicationContextAware, InitializingBean, DisposableBean {

	// 服务
	private ApplicationContext applicationContext;
	private Server server;
	private transient volatile boolean initialized;

	/**
	 * 销毁
	 */
	@Override
	public void destroy() throws Exception {
		server.stop();
	}

	/**
	 * 初始化执行
	 */
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
		
		// 启动服务
		server = new HttpServer(this);
		
		// 启动监控
		if (this.isStartReport()) {
			MetricCenter.report(applicationContext);
		}
	}

	@Override
	public Server getObject() throws Exception {
		if (server == null) {
			this.init();
		}
		return server;
	}

	@Override
	public Class<?> getObjectType() {
		return HttpServer.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}