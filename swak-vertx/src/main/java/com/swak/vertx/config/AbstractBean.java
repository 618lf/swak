package com.swak.vertx.config;

/**
 * 基本的配置
 * 
 * @author lifeng
 * @date 2020年8月23日 下午11:57:16
 */
public class AbstractBean implements AbstractConfig{

	protected int instances;

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}
}
