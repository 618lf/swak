package com.swak.mvc.method.converter;

import java.io.IOException;

import org.springframework.util.StreamUtils;

import com.swak.http.HttpConst;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * String 处理器
 * 返回值或者对象类型是 String
 * @author lifeng
 */
public class StringHttpMessageConverter implements HttpMessageConverter<String> {

	@Override
	public boolean canRead(Class<?> clazz) {
		return String.class == clazz;
	}

	@Override
	public String read(Class<? extends String> clazz, HttpServletRequest request) throws IOException {
		return StreamUtils.copyToString(request.getInputStream(), HttpConst.DEFAULT_CHARSET);
	}

	@Override
	public boolean canWrite(Class<?> clazz) {
		return String.class == clazz;
	}

	@Override
	public void write(String content, HttpServletResponse response) throws IOException {
		response.header(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		response.buffer(content);
	}
}