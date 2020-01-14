package com.swak.config.eventbus;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * 
 * EventBus 配置参数
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.EVENT_BUS_PREFIX)
public class EventBusProperties {

	private Integer coreThreads = 1;

	public Integer getCoreThreads() {
		return coreThreads;
	}

	public void setCoreThreads(Integer coreThreads) {
		this.coreThreads = coreThreads;
	}
}
