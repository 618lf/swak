package com.swak.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * 缓存属性配置
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.CACHE_PREFIX)
public class CacheProperties {

	private String hosts;
	private String password;
	
	// 二级缓存
	private String localName = "LOCAL";
	private int localElements = 2000;
	private int localLiveSeconds = 600;
	
	public int getLocalLiveSeconds() {
		return localLiveSeconds;
	}
	public void setLocalLiveSeconds(int localLiveSeconds) {
		this.localLiveSeconds = localLiveSeconds;
	}
	public String getLocalName() {
		return localName;
	}
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	public int getLocalElements() {
		return localElements;
	}
	public void setLocalElements(int localElements) {
		this.localElements = localElements;
	}
	public String getHosts() {
		return hosts;
	}
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
