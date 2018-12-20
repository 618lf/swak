package com.swak.reactivex.web.converter;

import java.io.IOException;

import com.swak.reactivex.transport.http.server.HttpServerResponse;

/**
 * 消息转换器
 * @author lifeng
 *
 * @param <T>
 */
public interface HttpMessageConverter {

	/**
	 * 写数据的判断
	 * @param clazz
	 * @return
	 */
	boolean canWrite(Class<?> clazz);
	
	/**
	 * 输出响应
	 * @param t
	 * @param response
	 * @throws IOException
	 */
	void write(Object t, HttpServerResponse response);
}
