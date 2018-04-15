package com.tmt.rx.my.v6;

/**
 * 转换
 * @author lifeng
 */
public interface Func<T, R> {
	 R call(T t);
}