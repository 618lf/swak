package com.swak.persistence.ms;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * 代理真实的数据源
 * 
 * @author lifeng
 * @date 2020年4月29日 下午5:27:31
 */
public class DataSourceWraper implements DataSource {

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new RuntimeException("This DataSource Not Support Db Operation.");
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new RuntimeException("This DataSource Not Support Db Operation.");
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new RuntimeException("This DataSource Not Support Db Operation.");
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw new RuntimeException("This DataSource Not Support Db Operation.");
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new RuntimeException("This DataSource Not Support Db Operation.");
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new RuntimeException("This DataSource Not Support Db Operation.");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new RuntimeException("This DataSource Not Support Db Operation.");
	}

	@Override
	public Connection getConnection() throws SQLException {
		throw new RuntimeException("This DataSource Not Support Db Operation.");
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new RuntimeException("This DataSource Not Support Db Operation.");
	}
}
