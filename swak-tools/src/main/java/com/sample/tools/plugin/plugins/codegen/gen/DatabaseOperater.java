package com.sample.tools.plugin.plugins.codegen.gen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.sample.tools.plugin.plugins.codegen.gen.dialects.MsSqlOperater;
import com.sample.tools.plugin.plugins.codegen.gen.dialects.MySqlOperater;
import com.swak.utils.Lists;

/**
 * 数据库方言
 * 
 * @author lifeng
 */
public interface DatabaseOperater {

	/**
	 * 获得所有的数据库
	 * 
	 * @param db
	 * @return
	 */
	List<String> getDbs();

	/**
	 * 获得所有的表
	 * 
	 * @param db
	 * @return
	 */
	List<String> getTables(String db);

	/**
	 * 获得指定的表
	 * 
	 * @param db
	 * @param table
	 * @return
	 */
	Table getTable(String db, String table);

	/**
	 * 打开链接
	 */
	void open();

	/**
	 * 关闭连接
	 */
	void close();

	/**
	 * 是否可操作的
	 * 
	 * @return
	 */
	boolean isActive();

	/**
	 * 执行Sql
	 * 
	 * @param sql
	 * @throws SQLException
	 */
	default void execSql(Connection conn, String sql) {
		PreparedStatement stmt = null;
		try {
			Boolean autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(Boolean.FALSE);
			stmt = conn.prepareStatement(sql);
			stmt.execute();
			conn.setAutoCommit(autoCommit);
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * 执行Sql
	 * 
	 * @param sql
	 * @throws SQLException
	 */
	default <T> List<T> execSql(Connection conn, String sql, RowSetMapper<T> mapper) {
		List<T> ts = Lists.newArrayList();
		PreparedStatement stmt = null;
		ResultSet result = null;
		try {
			Boolean autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(Boolean.FALSE);
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			int row = 0;
			while (result.next()) {
				T t = mapper.mapRow(result, row++);
				ts.add(t);
			}
			conn.setAutoCommit(autoCommit);
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			try {
				result.close();
				stmt.close();
			} catch (SQLException e) {
			}
		}
		return ts;
	}

	/**
	 * 打开数据库链接
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	default Connection open(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * 创建一个数据库操作
	 * 
	 * @param db
	 * @param ip
	 * @param port
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public static DatabaseOperater build(String db, String ip, String port, String userName, String passWord) {
		if ("MSSQL".equalsIgnoreCase(db)) {
			return new MsSqlOperater(ip, port, userName, passWord);
		}
		if ("MYSQL".equalsIgnoreCase(db)) {
			return new MySqlOperater(ip, port, userName, passWord);
		}
		return null;
	}
}
