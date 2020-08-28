package com.swak.vertx.config;

import javax.annotation.PostConstruct;

public interface AbstractConfig {

	/**
	 * 初始化操作, 添加到配置管理器
	 */
	@PostConstruct
	default void addIntoConfigManager() {
		VertxConfigs.me().add(this);
	}
}
