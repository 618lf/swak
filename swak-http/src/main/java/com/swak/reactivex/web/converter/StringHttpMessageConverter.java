package com.swak.reactivex.web.converter;

import java.io.IOException;

import org.springframework.util.StreamUtils;

import com.swak.reactivex.HttpConst;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;

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
	public String read(Class<? extends String> clazz, HttpServerRequest request) throws IOException {
		return StreamUtils.copyToString(request.getInputStream(), HttpConst.DEFAULT_CHARSET);
	}

	@Override
	public boolean canWrite(Class<?> clazz) {
		return String.class == clazz;
	}

	@Override
	public void write(String content, HttpServerResponse response) throws IOException {
		response.text().buffer(content);
	}
}