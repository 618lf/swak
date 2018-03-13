package com.swak.common.cache.collection;

/**
 * 序列化策略
 * @author lifeng
 */
public interface SerStrategy {

	/**
	 * 序例化的方式
	 * @param t
	 * @return
	 */
	<T> byte[] serialize(T t);
	
	/**
	 * 序例化的方式
	 * @param t
	 * @return
	 */
	<T> T deserialize(byte[] bytes);
}
