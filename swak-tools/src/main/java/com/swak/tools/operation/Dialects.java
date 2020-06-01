package com.swak.tools.operation;

import com.swak.tools.operation.dialect.Dialect;
import com.swak.tools.operation.dialect.H2Dialect;
import com.swak.tools.operation.dialect.SqliteDialect;

/**
 * 根据配置自动适配
 * 
 * @author lifeng
 */
public class Dialects {

	/**
	 * 数据库配置
	 * 
	 * @param datasource
	 * @return
	 */
	public static Dialect adapted(String db) {
		if (db.equals("h2")) {
			return new H2Dialect();
		}
		return new SqliteDialect();

	}
}
