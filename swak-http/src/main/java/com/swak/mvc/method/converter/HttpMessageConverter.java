package com.swak.mvc.method.converter;

import java.io.IOException;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

/**
 * 消息转换器
 * @author lifeng
 *
 * @param <T>
 */
public interface HttpMessageConverter<T> {

	/**
	 * 读取的判断
	 * @param clazz
	 * @return
	 */
	boolean canRead(Class<?> clazz);

	/**
	 * 读取数据并返回响应的值
	 * @param clazz
	 * @param request
	 * @return
	 * @throws IOException
	 */
	T read(Class<? extends T> clazz, HttpServletRequest request) throws IOException;

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
	void write(T t, HttpServletResponse response) throws IOException;
}
