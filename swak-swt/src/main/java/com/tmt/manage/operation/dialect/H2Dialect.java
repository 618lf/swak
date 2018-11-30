package com.tmt.manage.operation.dialect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.tmt.manage.operation.Dialect;

/**
 * 
 * @author lifeng
 */
public class H2Dialect implements Dialect{

	/**
	 * 打开h2数据库
	 * @throws SQLException 
	 */
	@Override
	public Connection open(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url,user,password);
	}
}
