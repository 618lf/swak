package com.swak.vertx.config;

import javax.annotation.PostConstruct;

/**
 * 基本的配置
 * 
 * @author lifeng
 * @date 2020年8月23日 下午11:57:16
 */
public interface AbstractConfig {

	/**
	 * 初始化操作, 添加到配置管理器
	 */
	@PostConstruct
	default void addIntoConfigManager() {
		VertxConfigs.me().add(this);
	}
}
