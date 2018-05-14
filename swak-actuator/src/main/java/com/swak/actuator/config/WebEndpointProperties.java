package com.swak.actuator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.common.Constants;

@ConfigurationProperties(prefix = Constants.ACTUATOR_ENDPOINT_WEB)
public class WebEndpointProperties {

	/**
	 * 基础路径
	 */
	private String rootPath = "/actuator";

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
}
