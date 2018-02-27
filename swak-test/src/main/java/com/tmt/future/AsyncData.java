package com.tmt.future;

/**
 * 返回数据
 * 
 * @author lifeng
 */
public interface AsyncData<T> {

	/**
	 * 获取数据并返回自己
	 * 
	 * @return
	 */
	AsyncData<T> fetch();

	/**
	 * 返回数据
	 * 
	 * @return
	 */
	T get();
}