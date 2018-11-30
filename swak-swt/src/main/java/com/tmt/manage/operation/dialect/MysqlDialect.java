package com.tmt.manage.operation.dialect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.tmt.manage.operation.Dialect;

/**
 * Mysql
 * 
 * @author lifeng
 */
public class MysqlDialect implements Dialect{

	@Override
	public Connection open(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url,user,password);
	}
}