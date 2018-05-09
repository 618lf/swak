package com.swak.common.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * 数据源管理器
 * 使用的时候请保证生命周期
 * @author liFeng 2014年6月23日
 */
public class DataSourceHolder {

	private static DataSource dataSource = null;
	
	/**
	 * 设置
	 * @param dataSource
	 */
	public static void setDataSource(DataSource dataSource) {
		DataSourceHolder.dataSource = dataSource;
	}
	
	/**
	 * 获取一个新的链接
	 * @return
	 * @throws CannotGetJdbcConnectionException
	 */
	public static Connection getConnection() throws CannotGetJdbcConnectionException {
		try {
			return dataSource.getConnection();
		} catch (SQLException ex) {
			throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", ex);
		}
	}
	
	/**
	 * 释放此链接
	 * @param con
	 */
	public static void releaseConnection(Connection con) {
		DataSourceUtils.releaseConnection(con, dataSource);
	}
}