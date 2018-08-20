package com.swak.vertx.handler.converter;

import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.http.HttpServerResponse;

/**
 * String 处理器 返回值或者对象类型是 String
 * 
 * @author lifeng
 */
public class StringHttpMessageConverter implements HttpMessageConverter {

	@Override
	public boolean canWrite(Class<?> clazz) {
		return String.class == clazz;
	}

	@Override
	public void write(Object content, HttpServerResponse response) {
		response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		response.end((String)content);
	}
}