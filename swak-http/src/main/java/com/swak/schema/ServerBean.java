package com.swak.schema;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.swak.http.Server;
import com.swak.http.server.HttpServer;

/**
 * 服务
 * 
 * @author lifeng
 */
public class ServerBean extends HttpServer.Builder implements FactoryBean<Server>, InitializingBean, DisposableBean {

	// 服务
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
		server = new HttpServer(this);
		server.start();
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
}