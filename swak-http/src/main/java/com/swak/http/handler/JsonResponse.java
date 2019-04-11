package com.swak.http.handler;

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
		throw new RuntimeException(response.toString());
	}
	
	/**
	 * 对象
	 * @return
	 */
	public static <T> JsonResponse<T> create(Class<T> clazz) {
		return new JsonResponse<T>().use(clazz);
	}
}