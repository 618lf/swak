package com.swak.fx.operation.dialect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.swak.fx.operation.Dialect;

/**
 * Mysql
 * 
 * @author lifeng
 */
public class MysqlDialect implements Dialect {

	@Override
	public Connection open(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url,user,password);
	}

	/**
	 * 不支持的备份
	 */
	@Override
	public void backup(String url, String user, String password) throws SQLException {
		
	}

	@Override
	public String db() {
		return "mysql";
	}
}