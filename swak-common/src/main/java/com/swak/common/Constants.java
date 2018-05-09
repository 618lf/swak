package com.swak.common;

/**
 * 系统常量
 * @author lifeng
 */
public interface Constants {
	
	/**  配置项前缀 */
	String APPLICATION_PREFIX = "spring.application";
	String HTTP_SERVER_PREFIX = "spring.http-server";
	String CACHE_PREFIX = "spring.cache";
	String SECURITY_PREFIX = "spring.security";
	String MYBATIS_PREFIX = "spring.mybatis";
	String DATASOURCE_PREFIX = "spring.datasource";
	
	/** 用户密码加密 **/
	String HASH_ALGORITHM = "SHA-1";
	int HASH_INTERATIONS = 1024;
	int SALT_SIZE = 8;
	
	/** 保留ID **/
	Long ROOT = 0L;

	/** 默认编码 **/
	String DEFAULT_ENCODING = "UTF-8";
	
	/** 系统通道 **/
	String LOCAL_CACHE_TOPIC = "LOCAL_CACHE_TOPIC";
	String UPDATE_EVENT_TOPIC = "UPDATE_EVENT_TOPIC";
}
