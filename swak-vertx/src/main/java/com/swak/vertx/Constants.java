package com.swak.vertx;

/**
 * 系统常量
 * 
 * @author lifeng
 */
public interface Constants {
	String SUBJECT_NAME = "X-Subject";
	String TOKEN_NAME = "X-Token";
	String ASYNC_SUFFIX = "Asyncx";
	String _ASYNC_SUFFIX = "Async"; // 兼容之前的系统，匹配这个后缀
}
