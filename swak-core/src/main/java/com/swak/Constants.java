package com.swak;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 系统常量
 * 
 * @author lifeng
 */
public interface Constants {

	/** url path separate */
	String URL_PATH_SEPARATE = "/";

	/** 配置项前缀 */
	String APPLICATION_PREFIX = "spring.application";
	String RPC_SERVER_PREFIX = "spring.rpc-server";
	String HTTP_SERVER_PREFIX = "spring.http-server";
	String CACHE_PREFIX = "spring.cache";
	String HTTP_CLIENT_PREFIX = "spring.http-client";
	String SECURITY_PREFIX = "spring.security";
	String MYBATIS_PREFIX = "spring.mybatis";
	String DATASOURCE_PREFIX = "spring.datasource";
	String ACTUATOR_METRICS = "spring.metrics";
	String ACTUATOR_ENDPOINT_WEB = "spring.endpoints.web";
	String ACTUATOR_TRACE = "spring.trace";
	String STORAGE_PREFIX = "spring.storage";
	String MOTAN_PREFIX = "spring.motan";

	/** 保留ID **/
	long ROOT = 0L;
	long INVALID_ID = -1;

	/** 默认编码 **/
	Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

	/** 系统通道 **/
	String LOCAL_CACHE_TOPIC = "LOCAL_CACHE_TOPIC";
	String UPDATE_EVENT_TOPIC = "UPDATE_EVENT_TOPIC";
	String SYSTEM_EVENT_TOPIC = "SYSTEM_EVENT_TOPIC";
	String ACCESS_TRACE = "ACCESS_TRACE";
	String LOCK_TOPIC = "LOCK_TOPIC";

	/** 事件类型 **/
	int SIGN_IN = 1; // 登录
	int SIGN_UP = 2; // 注册
	int LOGOUT = 3; // 退出

	/** 缓存 **/
	int cookie_cache_times = 60 * 60 * 24;
	String token_cache_name = "TOKENS";
	String deleted_cookie_value = "deleteMe";
	String session_name = "SESSION";

	/** 线程池 **/
	String default_pool = "default";
	String write_pool = "write";
	String read_pool = "read";
	String single_pool = "single";
}
