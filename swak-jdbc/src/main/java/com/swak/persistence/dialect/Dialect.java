package com.swak.persistence.dialect;

import java.util.Map;

import com.swak.persistence.Database;
import com.swak.utils.Maps;

/**
 * 方言
 * 
 * @author lifeng
 */
public interface Dialect {

	public boolean supportsLimit();

	public String getLimitString(String sql, boolean hasOffset);

	public String getLimitString(String sql, int offset, int limit);
	
	/**
	 * 默认 Mysql ，会根据mysql 定义一些必要的命令集
	 * 
	 * @return
	 */
	default Database getDatabase() {
		return Database.mysql;
	}

	/**
	 * 定义一些特殊的全局变量
	 * 
	 * @return
	 */
	default Map<String, String> variables() {
		return Maps.newHashMap();
	}
}