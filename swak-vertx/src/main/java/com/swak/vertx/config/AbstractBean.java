package com.swak.vertx.config;

import org.springframework.beans.factory.InitializingBean;

/**
 * 基本的配置
 * 
 * @author lifeng
 * @date 2020年8月23日 下午11:57:16
 */
public abstract class AbstractBean implements AbstractConfig, InitializingBean {

	private boolean inited = false;
	protected int instances;

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}

	/**
	 * 保证初始化的顺序
	 * 
	 * @throws Exception
	 */
	@Override
	public void addIntoConfigManager() throws Exception {

		// 优先完成初始化
		this.afterPropertiesSet();

		// 添加到组件管理器
		AbstractConfig.super.addIntoConfigManager();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.inited) {
			return;
		}

		this.initializing();

		this.inited = true;
	}

	/**
	 * 初始化
	 */
	protected abstract void initializing() throws Exception;
}
