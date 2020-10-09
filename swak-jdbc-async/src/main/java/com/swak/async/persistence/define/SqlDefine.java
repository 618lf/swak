package com.swak.async.persistence.define;

import com.swak.async.persistence.Sql;

/**
 * Sql 定义
 * 
 * @author lifeng
 * @date 2020年10月8日 下午6:54:38
 */
public class SqlDefine<T> extends NameDefine {

	/**
	 * SQL 脚本
	 */
	public Sql<T> sql;

	public SqlDefine(String name, Sql<T> sql) {
		this.name = name;
		this.sql = sql;
	}
}
