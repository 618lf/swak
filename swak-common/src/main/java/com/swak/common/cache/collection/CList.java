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
	
	/**
	 * 设置过期时间
	 * @param seconds
	 * @return
	 */
	CList<T> expire(int seconds);
	
	/**
	 * 设置为原型类型的list
	 * @return
	 */
	CList<String> primitive();
	
	/**
	 * 设置为复杂类型的list
	 * @return
	 */
	CList<T> complex();
}