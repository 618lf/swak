package com.swak.common.http;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.common.Constants;

/**
 * httpclient 配置
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.HTTP_CLIENT_PREFIX)
public class HttpClientProperties {

	private String userAgent = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.186 Safari/535.1";

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}