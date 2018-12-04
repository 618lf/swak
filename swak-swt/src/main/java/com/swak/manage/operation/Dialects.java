package com.swak.manage.operation;

import com.swak.manage.operation.dialect.H2Dialect;
import com.swak.manage.operation.dialect.MysqlDialect;
import com.swak.manage.operation.dialect.SqliteDialect;

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
		if ("mysql".equals(db)) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (Exception e) {
				try {
					Class.forName("com.mysql.jdbc.Driver");
				} catch (ClassNotFoundException e1) {
				}
			}
			return new MysqlDialect();
		} else if ("h2".equals(db)) {
			try {
				Class.forName("org.h2.Driver");
			} catch (ClassNotFoundException e1) {
			}
			return new H2Dialect();
		} else if ("sqlite".equals(db)) {
			return new SqliteDialect();
		}
		return null;
	}
}
