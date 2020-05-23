package com.swak.oss;

import java.net.URI;
import java.util.Map;

/**
 * Oss 配置
 * 
 * @author lifeng
 */
public interface OssConfig {

	/**
	 * 存储的节点
	 * 
	 * @return
	 */
	URI getEndpoint();

	/**
	 * APP Key
	 * 
	 * @return
	 */
	String getAccessKeyId();

	/**
	 * APP Secret
	 * 
	 * @return
	 */
	String getAccessKeySecret();

	/**
	 * 存储的位置
	 * 
	 * @return
	 */
	Map<String, Bucket> getBuckets();

	/**
	 * 不同的类型存储在不同的地方
	 * 
	 * @author lifeng
	 */
	public static class Bucket {
		private String name;
		private String domain;
		private boolean auth = true;

		public boolean isAuth() {
			return auth;
		}

		public void setAuth(boolean auth) {
			this.auth = auth;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public static Bucket newBucket(String name) {
			Bucket bucket = new Bucket();
			bucket.setName(name);
			return bucket;
		}

		public static Bucket newBucket(String name, String domain) {
			Bucket bucket = new Bucket();
			bucket.setName(name);
			bucket.setDomain(domain);
			return bucket;
		}

		public static Bucket newBucket(String name, String domain, boolean auth) {
			Bucket bucket = new Bucket();
			bucket.setName(name);
			bucket.setDomain(domain);
			bucket.setAuth(auth);
			return bucket;
		}
	}
}