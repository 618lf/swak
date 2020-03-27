package com.swak;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 系统常量
 * 
 * @author lifeng
 */
public interface Constants {

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
	String EVENT_BUS_PREFIX = "spring.eventbus";
	String RXTX_PREFIX = "spring.rxtx";

	/** 保留ID **/
	long ROOT = 0L;
	long INVALID_ID = -1;

	/** 是/否/删除 - TINYINT **/
	byte YES = 1;
	byte NO = 0;
	byte DEL = -1;

	/** 默认编码 **/
	Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

	/** 缓存 (不建议使用) **/
	@Deprecated
	int cookie_cache_times = 60 * 60 * 24;
	@Deprecated
	String token_cache_name = "TOKENS";

	/** cookie **/
	String DELETED_COOKIE_VALUE = "deleteMe";

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
	String ASYNC_SUFFIX = "Async";

	/** validate msg **/
	String NotNullErrorMsg = "非空校验失败";
	String MaxErrorMsg = "最大值校验失败";
	String MinErrorMsg = "最小值校验失败";
	String LengthErrorMsg = "长度校验失败";
	String EmailErrorMsg = "邮箱校验失败";
	String PhoneNumErrorMsg = "手机号校验失败";
	String RegexErrorMsg = "正则校验失败";

	/** validate regex **/
	String DATE_REGEX = "^[0-9]{4}\\-[0-9]{1,2}\\-[0-9]{1,2}$";
	String DIGIT_REGEX = "^\\d+(\\.\\d+)?$";
	String IP_REGEX = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
	String EMAIL_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	String PHONE_NUM_REGEX = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";

	/** permission **/
	String security_OR_DIVIDER_TOKEN = "|";
	String security_AND_DIVIDER_TOKEN = ",";
	String security_WILDCARD_TOKEN = "*";
	String security_PART_DIVIDER_TOKEN = ":";

	/** 目录 **/
	String tempDir = System.getProperty("java.io.tmpdir");
}
