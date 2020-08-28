package io.vertx.core.http.impl;

import io.vertx.core.MultiMap;

/**
 * 可以获取一些内部方法
 * 
 * @author lifeng
 * @date 2020年8月28日 下午1:17:41
 */
public class VertxHttpUtils {

	/**
	 * 参数
	 * 
	 * @param uri
	 * @return
	 */
	public static MultiMap params(String uri) {
		return HttpUtils.params(uri);
	}
}