package com.swak.common.http.handler;

import java.nio.charset.Charset;

import org.asynchttpclient.Response;

import com.swak.common.utils.JaxbMapper;

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
	public static <T> XmlResponse<T> object(Class<T> clazz) {
		return new XmlResponse<T>().use(clazz);
	}
	
	/**
	 * gbk
	 * @return
	 */
	public static <T> XmlResponse<T> gbk() {
		return new XmlResponse<T>().use(Charset.forName("gbk"));
	}
	
	/**
	 * utf-8
	 * @return
	 */
	public static <T> XmlResponse<T> utf8() {
		return new XmlResponse<T>().use(Charset.forName("utf-8"));
	}
	
	/**
	 * 自定义编码
	 * @param charset
	 * @return
	 */
	public static <T> XmlResponse<T> charset(Charset charset) {
		return new XmlResponse<T>().use(charset);
	}
}