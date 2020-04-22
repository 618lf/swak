package com.swak.method.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.swak.entity.IdEntity;

/**
 * 通用的服务类
 * 
 * @author lifeng
 * @date 2020年4月20日 上午11:13:02
 */
public interface BaseService<T extends IdEntity<PK>, PK extends Serializable, Ts, FIX, FIXS>
		extends SuperService<PK, T, List<T>, FIX, FIXS> {

	/**
	 * 测试泛型
	 * 
	 * @param entity
	 */
	default List<T> B(T entity, Ts entitys, Map<String, Object> params, PK id, FIX fix, FIXS fixs) {
		return null;
	}

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