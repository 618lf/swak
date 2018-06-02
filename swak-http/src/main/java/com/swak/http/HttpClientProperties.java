package com.swak.http;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * httpclient 配置
 * 超时时间可以设置大一点
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.HTTP_CLIENT_PREFIX)
public class HttpClientProperties {

	private String userAgent = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.186 Safari/535.1";
    private int connectTimeout = 60000;
    private int requestTimeout = 60000;
    private int readTimeout = 60000;
    private int handshakeTimeout = 60000;

	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public int getRequestTimeout() {
		return requestTimeout;
	}
	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public int getHandshakeTimeout() {
		return handshakeTimeout;
	}
	public void setHandshakeTimeout(int handshakeTimeout) {
		this.handshakeTimeout = handshakeTimeout;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}