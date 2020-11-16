package com.swak.persistence.dialect;

import java.util.Map;

import com.swak.utils.Maps;

/**
 * 方言
 * 
 * @author lifeng
 */
public interface Dialect {

	/**
	 * 是否支持分页
	 * 
	 * @return
	 */
	boolean supportsLimit();

	/**
	 * 返回分页Sql
	 * 
	 * @param sql
	 * @param hasOffset
	 * @return
	 */
	String getLimitString(String sql, boolean hasOffset);

	/**
	 * 返回分页Sql
	 * 
	 * @param sql
	 * @param offset
	 * @param limit
	 * @return
	 */
	String getLimitString(String sql, int offset, int limit);

	/**
	 * 定义一些特殊的全局变量
	 * 
	 * @return
	 */
	default Map<String, String> variables() {
		return Maps.newHashMap();
	}
}