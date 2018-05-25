package com.swak.http.handler;

import java.nio.charset.Charset;

import org.asynchttpclient.Response;

import com.swak.utils.JsonMapper;

/**
 * 处理 json 数据的返回
 * @author lifeng
 */
public class JsonResponse<T> extends AbstractResponse<T>{

	/**
	 * 实现装换
	 */
	@Override
	public T onCompleted(Response response) throws Exception {
		int status = response.getStatusCode();
		if (status >= 200 && status < 300) {
			String str = response.getResponseBody(charset);
			return JsonMapper.fromJson(str, clazz);
		}
		return null;
	}
	
	/**
	 * 对象
	 * @return
	 */
	public static <T> JsonResponse<T> object(Class<T> clazz) {
		return new JsonResponse<T>().use(clazz);
	}
	
	/**
	 * gbk
	 * @return
	 */
	public static <T> JsonResponse<T> gbk() {
		return new JsonResponse<T>().use(Charset.forName("gbk"));
	}
	
	/**
	 * utf-8
	 * @return
	 */
	public static <T> JsonResponse<T> utf8() {
		return new JsonResponse<T>().use(Charset.forName("utf-8"));
	}
	
	/**
	 * 自定义编码
	 * @param charset
	 * @return
	 */
	public static <T> JsonResponse<T> charset(Charset charset) {
		return new JsonResponse<T>().use(charset);
	}
}