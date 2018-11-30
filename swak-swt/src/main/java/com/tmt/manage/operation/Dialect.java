package com.tmt.manage.operation;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库方言
 * 
 * @author lifeng
 */
public interface Dialect {

	/**
	 * 打开数据库链接
	 * 
	 * @param datasource
	 * @return
	 */
	Connection open(String url, String user, String password) throws SQLException;
	
}
