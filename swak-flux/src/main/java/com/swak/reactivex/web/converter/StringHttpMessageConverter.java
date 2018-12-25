package com.swak.reactivex.web.converter;

import com.swak.Constants;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.utils.StringUtils;

import io.netty.handler.codec.http.HttpHeaderNames;

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
		String _content = (String) content;
		if (_content != null && StringUtils.startsWith(_content, Constants.REDIRECT_URL_PREFIX)) {
			String payload = StringUtils.substringAfter(_content,Constants.REDIRECT_URL_PREFIX);
			response.redirect().header(HttpHeaderNames.LOCATION, payload).buffer("Redirecting to " + payload + ".");
			return;
		}
		response.text().accept().buffer(content);
	}
}