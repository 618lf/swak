package com.swak.persistence.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * 数据源管理器 使用的时候请保证生命周期
 * 
 * @author liFeng 2014年6月23日
 */
public class DataSourceHolder {

	private static DataSource dataSource = null;

	/**
	 * 获取
	 * 
	 * @return
	 */
	public static DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * 设置
	 * 
	 * @param dataSource
	 */
	public static void setDataSource(DataSource dataSource) {
		DataSourceHolder.dataSource = dataSource;
	}

	/**
	 * 获取一个新的链接
	 * 
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
	 * 
	 * @param con
	 */
	public static void releaseConnection(Connection con) {
		DataSourceUtils.releaseConnection(con, dataSource);
	}

	/**
	 * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
	 * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
	 */
	private static final ThreadLocal<Object> CONTEXT_HOLDER = new ThreadLocal<>();

	/**
	 * 设置数据源的变量
	 */
	public static void setDataSourceType(Object dsType) {
		CONTEXT_HOLDER.set(dsType);
	}

	/**
	 * 获得数据源的变量
	 */
	public static Object getDataSourceType() {
		return CONTEXT_HOLDER.get();
	}

	/**
	 * 清空数据源变量
	 */
	public static void clearDataSourceType() {
		CONTEXT_HOLDER.remove();
	}
}