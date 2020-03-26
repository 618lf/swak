package com.swak.oss;

import java.io.Serializable;

import com.swak.oss.OssConfig.Bucket;
import com.swak.utils.JsonMapper;

/**
 * 响应
 * 
 * @author lifeng
 */
public class OssResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer statusCode;
	private Bucket bucket;
	private String resourcePath;
	private Integer size;
	private String signedUrl;
	private String errorMessgae;

	public String getErrorMessgae() {
		return errorMessgae;
	}

	public void setErrorMessgae(String errorMessgae) {
		this.errorMessgae = errorMessgae;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public Bucket getBucket() {
		return bucket;
	}

	public void setBucket(Bucket bucket) {
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

	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}

	/**
	 * 转为数据存储
	 * 
	 * @return
	 */
	public OssStore toStore() {
		OssStore ossStore = new OssStore();
		ossStore.setBucket(bucket.getName());
		ossStore.setResourcePath(resourcePath);
		if (!bucket.isAuth()) {
			ossStore.setSignedUrl(signedUrl);
		}
		return ossStore;
	}
}
