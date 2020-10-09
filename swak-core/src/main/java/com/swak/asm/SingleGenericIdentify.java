package com.swak.asm;

import java.lang.reflect.ParameterizedType;

/**
 * 泛型识别
 * 
 * @author lifeng
 * @date 2020年10月7日 下午9:53:38
 */
public interface SingleGenericIdentify<T> {

	/**
	 * 目标对象 T 的实际类型
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	default Class<T> getEntityClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
}