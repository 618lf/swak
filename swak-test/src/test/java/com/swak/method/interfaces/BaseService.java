package com.swak.method.interfaces;

import java.io.Serializable;

/**
 * 通用的服务类
 * 
 * @author lifeng
 * @date 2020年4月20日 上午11:13:02
 */
public interface BaseService<T extends Serializable, PK extends Serializable> {

	/**
	 * 测试泛型
	 * 
	 * @param entity
	 */
	default T testGeneric(T entity, PK id) {
		return null;
	}

	/**
	 * 测试 Class<?>
	 * 
	 * @param entity
	 */
	default T testClassGeneric(T entity, Class<T> type) {
		return null;
	}
}