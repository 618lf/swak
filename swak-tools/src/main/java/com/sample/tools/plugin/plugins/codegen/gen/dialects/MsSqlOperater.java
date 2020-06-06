package com.sample.tools.plugin.plugins.codegen.gen.dialects;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sample.tools.plugin.plugins.codegen.gen.DatabaseOperater;
import com.swak.utils.StringUtils;

/**
 * mssql 的表操作
 * 
 * @author lifeng
 * @date 2020年6月4日 下午9:44:34
 */
public class MsSqlOperater implements DatabaseOperater, Closeable {

	private final String FETCH_DBS_SQL = "SELECT NAME FROM SysDatabases where dbid>4 ORDER BY Name";
	private final String FETCH_TABLES_SQL = "SELECT NAME FROM SysObjects Where XType='U' and (Name not like 'sys%' and Name not like 'MSp%') ORDER BY NAME";
	private final String URL_TEMPLATE = "jdbc:sqlserver://%s:%s";
	private String ip;
	private String port;
	private String url;
	private String userName;
	private String passWord;
	private Connection conn;
	private AtomicBoolean opened = new AtomicBoolean(false);

	public MsSqlOperater(String ip, String port, String userName, String passWord) {
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.passWord = passWord;
	}

	/**
	 * 获得所有的数据库
	 */
	@Override
	public List<String> getDbs() {
		if (!this.opened.get()) {
			this.open();
		}
		return this.execSql(conn, FETCH_DBS_SQL, (rs, sn) -> {
			return rs.getString("NAME");
		});
	}

	@Override
	public List<String> getTables(String db) {
		if (!this.opened.get()) {
			this.open();
		}
		this.execSql(conn, "USE " + db);
		return this.execSql(conn, FETCH_TABLES_SQL, (rs, sn) -> {
			return rs.getString("NAME");
		});
	}

	@Override
	public void open() {
		if (this.opened.get()) {
			this.close();
		}
		this.tryOpen();
	}

	private void tryOpen() {
		try {
			this.url = StringUtils.format(URL_TEMPLATE, this.ip, this.port);
			conn = this.open(url, userName, passWord);
			this.opened.set(true);
		} catch (Exception e) {
			conn = null;
		}
	}

	@Override
	public void close() {
		try {
			this.opened.set(false);
			conn.close();
		} catch (Exception e) {
		}
	}

	@Override
	public boolean isActive() {
		return this.opened.get();
	}
}