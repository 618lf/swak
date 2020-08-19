package com.swak;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 系统常量
 *
 * @author: lifeng
 * @date: 2020/3/29 15:41
 */
public interface Constants {

	/**
	 * Path
	 */
	String PATH_SEPARATE = "/";
	String IDS_SEPARATE = ",";

	/**
	 * Http Path 配置项目
	 */
	String URL_PATH_SEPARATE = "/";
	String URL_PATH_VARIABLE_PRE = "{";
	String URL_PATH_VARIABLE_SUFFIX = "}";
	String URL_PATH_VARIABLE_PRE_X = ":";
	String URL_PATH_VARIABLE_SUFFIX_X = "";

	/**
	 * 配置项前缀
	 */
	String APPLICATION_PREFIX = "spring.application";
	String VERTX_SERVER_PREFIX = "spring.vertx";
	String FLUX_SERVER_PREFIX = "spring.http-server";
	String REDIS_PREFIX = "spring.redis";
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
	String AOP_PREFIX = "spring.aop";

	/**
	 * 保留ID
	 **/
	long ROOT = 0L;
	long INVALID_ID = -1;
	String STR_INVALID_ID = "-1";

	/**
	 * 是/否/删除 - TINYINT
	 **/
	byte YES = 1;
	byte NO = 0;
	byte DEL = -1;

	/**
	 * 默认编码
	 **/
	Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

	/**
	 * 缓存 (不建议使用)
	 **/
	@Deprecated
	int COOKIE_CACHE_TIMES = 60 * 60 * 24;

	/**
	 * 缓存 (不建议使用)
	 */
	@Deprecated
	String TOKEN_CACHE_NAME = "TOKENS";

	/**
	 * cookie
	 **/
	String DELETED_COOKIE_VALUE = "deleteMe";

	/**
	 * 服务地址
	 **/
	String LOCALHOST = "127.0.0.1";

	/**
	 * 重定向
	 **/
	String REDIRECT_URL_PREFIX = "redirect:";

	/**
	 * 请求Key
	 **/
	String SUBJECT_NAME = "X-Subject";
	String VALIDATE_NAME = "X-Validate";
	String TOKEN_NAME = "X-Token";
	String EXCEPTION_NAME = "x-Exception";

	/**
	 * method key
	 **/
	String ASYNC_SUFFIX = "Async";

	/**
	 * validate msg
	 **/
	String NOT_NULL_ERROR_MSG = "非空校验失败";
	String MAX_ERROR_MSG = "最大值校验失败";
	String MIN_ERROR_MSG = "最小值校验失败";
	String LENGTH_ERROR_MSG = "长度校验失败";
	String EMAIL_ERROR_MSG = "邮箱校验失败";
	String PHONE_NUM_ERROR_MSG = "手机号校验失败";
	String REGEX_ERROR_MSG = "正则校验失败";

	/**
	 * validate regex
	 **/
	String DATE_REGEX = "^[0-9]{4}\\-[0-9]{1,2}\\-[0-9]{1,2}$";
	String DIGIT_REGEX = "^\\d+(\\.\\d+)?$";
	String IP_REGEX = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
	String EMAIL_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	String PHONE_NUM_REGEX = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";

	/**
	 * permission
	 **/
	String SECURITY_OR_DIVIDER_TOKEN = "|";
	String SECURITY_AND_DIVIDER_TOKEN = ",";
	String SECURITY_WILDCARD_TOKEN = "*";
	String SECURITY_PART_DIVIDER_TOKEN = ":";

	/**
	 * Sql 语句
	 **/
	String WHERE = "WHERE";

	/**
	 * Http Status
	 **/
	HttpResponseStatus FOUND = HttpResponseStatus.FOUND;
	HttpResponseStatus UNAUTHORIZED = HttpResponseStatus.UNAUTHORIZED;
	HttpResponseStatus NOT_FOUND = HttpResponseStatus.NOT_FOUND;
	HttpResponseStatus INTERNAL_SERVER_ERROR = HttpResponseStatus.INTERNAL_SERVER_ERROR;

	/**
	 * Invoker: 1 LocalMethod 、 2 异步
	 */
	byte OPERATORS_LOCAL = 1<<1;
	byte OPERATORS_ASYNC = 1<<2;
}
