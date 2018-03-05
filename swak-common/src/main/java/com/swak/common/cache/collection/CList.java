package com.swak.common.cache.collection;

/**
 * list 结构
 * @author lifeng
 * @param <T>
 */
public interface CList<T> {

	/**
	 * 插入一个元素
	 * @param t
	 */
	void push(T t);
	
	/**
	 * 取出一个元素
	 * @return
	 */
	T pop();
}