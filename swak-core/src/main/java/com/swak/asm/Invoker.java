package com.swak.asm;

/**
 * 目标是实现 对 method 实现 invoker
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
