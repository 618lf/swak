package com.swak.method;

/**
 * 通用的服务类
 * 
 * @author lifeng
 * @date 2020年4月20日 上午11:13:02
 */
public interface BaseService<T> {

	/**
	 * 测试泛型
	 * 
	 * @param entity
	 */
	default T testGeneric(T entity) {
		return null;
	}

	/**
	 * 测试 Class<?>
	 * 
	 * @param entity
	 */
	default T testGeneric(T entity, Class<T> type) {
		return null;
	}
}