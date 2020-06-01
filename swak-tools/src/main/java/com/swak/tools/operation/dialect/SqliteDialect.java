package com.swak.tools.operation.dialect;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.sqlite.SQLiteDataSource;

import com.swak.exception.BaseRuntimeException;
import com.swak.utils.StringUtils;

/**
 * Sqlite 数据库方言
 * 
 * @author lifeng
 * @date 2020年5月26日 上午11:53:23
 */
public class SqliteDialect implements Dialect {

	/**
	 * 打开h2数据库
	 * 
	 * @throws SQLException
	 */
	@Override
	public Connection open(String url, String user, String password) throws SQLException {
		SQLiteDataSource dataSource = new SQLiteDataSource();
		dataSource.setUrl("jdbc:sqlite:" + loadSqliteUrl(url));
		return dataSource.getConnection();
	}

	@Override
	public String db() {
		return "sqlite";
	}

	/**
	 * 加载资源文件
	 * 
	 * @return
	 */
	private String loadSqliteUrl(String location) {
		if (location.startsWith("resource:")) {
			return location;
		}
		try {
			return new File(StringUtils.substringAfter(location, "file:")).getAbsolutePath();
		} catch (Exception e) {
			throw new BaseRuntimeException(e);
		}
	}
}