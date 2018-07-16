package com.swak.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * 缓存属性配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.STORAGE_PREFIX)
public class StorageProperties {

	private String type = "local"; // 存储类型
	private String storagePath; // 存储的根目录
	private String urlPath;// 访问的前缀
	private String domain;// 域名

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
