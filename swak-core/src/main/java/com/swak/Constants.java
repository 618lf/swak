package com.swak;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.swak.utils.Lists;

/**
 * 系统常量
 * 
 * @author lifeng
 */
public interface Constants {

	/** 启动的类 */
	List<Class<?>> BOOT_CLASSES = Lists.newArrayList(1);

	/** Http Path 配置项目 */
	String URL_PATH_SEPARATE = "/";
	String URL_PATH_VARIABLE_PRE = "{";
	String URL_PATH_VARIABLE_SUFFIX = "}";
	String _URL_PATH_VARIABLE_PRE = ":";
	String _URL_PATH_VARIABLE_SUFFIX = "";

	/** 配置项前缀 */
	String APPLICATION_PREFIX = "spring.application";
	String VERTX_SERVER_PREFIX = "spring.vertx";
	String FLUX_SERVER_PREFIX = "spring.http-server";
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
	String QUARTZ_PREFIX = "spring.quartz";
	String RABBITMQ_PREFIX = "spring.rabbitmq";
	String ROCKETMQ_PREFIX = "spring.rocketmq";
	String MONGO_PREFIX = "spring.mongo";
	
	/** 保留ID **/
	long ROOT = 0L;
	long INVALID_ID = -1;
	
	/** 是/否/删除 - TINYINT **/
	byte YES = 1;
	byte NO = 0;
	byte DEL = -1;

	/** 默认编码 **/
	Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

	/** 缓存 **/
	int cookie_cache_times = 60 * 60 * 24;
	String token_cache_name = "TOKENS";
	String deleted_cookie_value = "deleteMe";

	/** 服务地址 **/
	String LOCALHOST = "127.0.0.1";

	/** 重定向 **/
	String REDIRECT_URL_PREFIX = "redirect:";

	/** 请求Key **/
	String SUBJECT_NAME = "X-Subject";
	String VALIDATE_NAME = "X-Validate";
	String TOKEN_NAME = "X-Token";
	String EXCEPTION_NAME = "x-Exception";

	/** method key **/
	String ASYNC_SUFFIX = "Asyncx";
	String _ASYNC_SUFFIX = "Async";

	/** validate msg **/
	String NotNullErrorMsg = "非空校验失败";
	String MaxErrorMsg = "最大值校验失败";
	String MinErrorMsg = "最小值校验失败";
	String LengthErrorMsg = "长度校验失败";
	String EmailErrorMsg = "邮箱校验失败";
	String PhoneNumErrorMsg = "手机号校验失败";
	String RegexErrorMsg = "正则校验失败";
	
	/** 目录 **/
	default String temp() {
		return System.getProperty("java.io.tmpdir");
	}
}
