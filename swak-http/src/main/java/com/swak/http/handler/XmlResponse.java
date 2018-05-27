package com.swak.http.handler;

import org.asynchttpclient.Response;

import com.swak.utils.JaxbMapper;

/**
 * 处理 xml 数据的返回
 * @author lifeng
 * @param <T>
 */
public class XmlResponse<T> extends AbstractResponse<T>{

	/**
	 * 实现装换
	 */
	@Override
	public T onCompleted(Response response) throws Exception {
		int status = response.getStatusCode();
		if (status >= 200 && status < 300) {
			String str = response.getResponseBody(charset);
			return JaxbMapper.fromXml(str, clazz);
		}
		return null;
	}
	
	/**
	 * 对象
	 * @return
	 */
	public static <T> XmlResponse<T> create(Class<T> clazz) {
		return new XmlResponse<T>().use(clazz);
	}
}