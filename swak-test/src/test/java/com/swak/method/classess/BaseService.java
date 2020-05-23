package com.swak.method.classess;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.swak.entity.IdEntity;
import com.swak.method.entity.Fix;

public class BaseService<T extends IdEntity<PK>, PK extends Serializable, Ts, FIX, FIXS>
		extends SuperService<PK, T, List<T>, Fix, List<Fix>> {

	/**
	 * 测试泛型
	 * 
	 * @param entity
	 */
	public List<T> B(T entity, Ts entitys, Map<String, Object> params, PK id, FIX fix, FIXS fixs) {
		return null;
	}

	/**
	 * 测试泛型
	 * 
	 * @param entity
	 */
	public List<T> testGeneric(T entity, Map<String, Object> params, PK id) {
		return null;
	}
//
//	/**
//	 * 测试泛型
//	 * 
//	 * @param entity
//	 */
//	public List<T> testGeneric(List<T> entitys, Map<String, Object> params, PK id) {
//		return null;
//	}
//
	/**
	 * 测试泛型
	 * 
	 * @param entity
	 */
	public List<Object> testGeneric(Object entity, Map<String, Object> params, Object id) {
		return null;
	}

//	/**
//	 * 测试泛型
//	 * 
//	 * @param entity
//	 */
//	public T testGeneric(List<T> entitys, PK id, Map<PK, T> maps) {
//		return null;
//	}
//
//	/**
//	 * 测试 Class<?>
//	 * 
//	 * @param entity
//	 */
//	public T testClassGeneric(T entity, Class<T> type) {
//		return null;
//	}
}
