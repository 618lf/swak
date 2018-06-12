package com.swak.rpc.invoker;

import com.swak.rpc.api.URL;

/**
 * Method Invoker
 * @author lifeng
 * @param <T>
 */
public interface Invoker<T> {
	
	/**
	 * 对应的唯一地址
	 * @return
	 */
	URL getURL();

	/**
	 * 执行
	 * @param params
	 * @return
	 */
	T invoke(Object... params);
}