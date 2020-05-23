package com.swak.oss;

import java.io.Serializable;

/**
 * 存储
 * 
 * @author lifeng
 */
public class OssStore implements Serializable {
	private static final long serialVersionUID = 1L;
	private String bucket;
	private String resourcePath;
	private String signedUrl;
	private Long addTime;
	private Integer expires_in;

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public String getSignedUrl() {
		return signedUrl;
	}

	public void setSignedUrl(String signedUrl) {
		this.signedUrl = signedUrl;
	}

	public Long getAddTime() {
		return addTime;
	}

	public void setAddTime(Long addTime) {
		this.addTime = addTime;
	}

	public Integer getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(Integer expires_in) {
		this.expires_in = expires_in;
	}
}
