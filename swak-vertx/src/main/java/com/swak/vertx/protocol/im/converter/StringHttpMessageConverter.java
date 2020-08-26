package com.swak.vertx.protocol.im.converter;

import com.swak.vertx.protocol.im.ImContext.ImResponse;
import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * String 处理器 返回值或者对象类型是 String
 *
 * @author: lifeng
 * @date: 2020/3/29 19:22
 */
public class StringHttpMessageConverter implements HttpMessageConverter {

	@Override
	public boolean canWrite(Class<?> clazz) {
		return String.class == clazz;
	}

	@Override
	public void write(Object t, ImResponse response) {
		response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		response.out((String) t);
	}
}