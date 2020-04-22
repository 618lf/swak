package com.swak.method.classess;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.swak.entity.IdEntity;

/**
 * 最高层次的
 * 
 * @author lifeng
 * @date 2020年4月21日 下午11:31:20
 */
public class SuperService<PK extends Serializable, T extends IdEntity<PK>, Ts, FIX, FIXS> {

	/**
	 * 测试泛型
	 * 
	 * @param entity
	 */
	public List<T> A(T entity, Ts entitys, Map<String, Object> params, PK id, FIX fix, FIXS fixs) {
		return null;
	}
}
