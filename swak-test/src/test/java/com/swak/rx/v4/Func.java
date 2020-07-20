package com.swak.rx.v4;

/**
 * 转换器
 * 
 * @author lifeng
 * @date 2020年7月19日 下午11:05:14
 */
public interface Func<T, R> {
	R call(T t);
}