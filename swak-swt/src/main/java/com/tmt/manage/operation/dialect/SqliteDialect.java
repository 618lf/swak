package com.tmt.manage.operation.dialect;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.core.io.FileSystemResource;
import org.sqlite.SQLiteDataSource;

import com.tmt.manage.operation.Dialect;

/**
 * 返回 sqllite 的数据库链接池
 * 
 * @author lifeng
 */
public class SqliteDialect implements Dialect {

	@Override
	public Connection open(String url, String user, String password) throws SQLException {
		SQLiteDataSource dataSource = new SQLiteDataSource();
		dataSource.setUrl("jdbc:sqlite:" + loadSqliteUrl(url));
		return dataSource.getConnection();
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
			FileSystemResource resource = new FileSystemResource(location);
			return resource.getFile().getAbsolutePath();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
