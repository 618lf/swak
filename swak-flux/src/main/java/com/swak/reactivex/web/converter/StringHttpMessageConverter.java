package com.swak.reactivex.web.converter;

import java.io.IOException;

import org.springframework.util.StreamUtils;

import com.swak.reactivex.transport.http.HttpConst;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;

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
	public String read(Class<? extends String> clazz, HttpServerRequest request) throws IOException{
		return StreamUtils.copyToString(request.getInputStream(), HttpConst.DEFAULT_CHARSET);
	}

	@Override
	public boolean canWrite(Class<?> clazz) {
		return String.class == clazz;
	}

	@Override
	public void write(String content, HttpServerResponse response) {
		response.text().accept().buffer(content);
	}
}