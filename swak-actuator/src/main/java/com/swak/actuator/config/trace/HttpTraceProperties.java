package com.swak.actuator.config.trace;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置 HttpTrace
 * @author lifeng
 */
@ConfigurationProperties(prefix = "ACCESS_TRACE")
public class HttpTraceProperties {
	
	private String storageMethod = "LOGGER";
	private String storageChannel = "ACCESS_TRACE";
	
	public String getStorageMethod() {
		return storageMethod;
	}
	public void setStorageMethod(String storageMethod) {
		this.storageMethod = storageMethod;
	}
	public String getStorageChannel() {
		return storageChannel;
	}
	public void setStorageChannel(String storageChannel) {
		this.storageChannel = storageChannel;
	}
}
