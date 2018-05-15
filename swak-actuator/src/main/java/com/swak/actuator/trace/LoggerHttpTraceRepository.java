package com.swak.actuator.trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.actuator.config.trace.HttpTraceProperties;
import com.swak.common.utils.JsonMapper;

/**
 * 基于日志的 Trace 存储
 * @author lifeng
 */
public class LoggerHttpTraceRepository implements HttpTraceRepository {
	
	final Logger logger;
	
	public LoggerHttpTraceRepository(HttpTraceProperties httpTraceProperties) {
		logger = LoggerFactory.getLogger(httpTraceProperties.getStorageChannel());
	}
	
	/**
	 * 已字符串的形式存储
	 */
	@Override
	public void storage(HttpTrace trace) {
		logger.info(JsonMapper.toJson(trace));
	}
}