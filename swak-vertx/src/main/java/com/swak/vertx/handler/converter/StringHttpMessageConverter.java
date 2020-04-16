package com.swak.vertx.handler.converter;

import com.swak.Constants;
import com.swak.utils.StringUtils;
import com.swak.vertx.transport.HttpConst;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;

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
	public void write(Object t, HttpServerResponse response) {
		String content = (String) t;
		if (content != null && StringUtils.startsWith(content, Constants.REDIRECT_URL_PREFIX)) {
			String payload = StringUtils.substringAfter(content, Constants.REDIRECT_URL_PREFIX);
			response.putHeader(HttpHeaders.LOCATION, payload).setStatusCode(302).end("Redirecting to " + payload + ".");
			return;
		}
		if (!response.headers().contains(HttpHeaderNames.CONTENT_TYPE)) {
			response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		}
		response.end(content);
	}
}