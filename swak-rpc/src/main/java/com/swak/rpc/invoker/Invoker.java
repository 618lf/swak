package com.swak.rpc.invoker;

/**
 * Method Invoker
 * @author lifeng
 * @param <T>
 */
public interface Invoker<T> {

	/**
	 * 执行
	 * @param params
	 * @return
	 */
	T invoke(Object... params);
}