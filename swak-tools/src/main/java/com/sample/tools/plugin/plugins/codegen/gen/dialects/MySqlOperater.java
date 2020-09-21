package com.sample.tools.plugin.plugins.codegen.gen.dialects;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sample.tools.plugin.plugins.codegen.gen.DatabaseOperater;
import com.sample.tools.plugin.plugins.codegen.gen.Table;
import com.sample.tools.plugin.plugins.codegen.gen.TableColumn;
import com.swak.utils.StringUtils;

/**
 * Mysql 的代码生成工具
 * 
 * @author lifeng
 * @date 2020年9月21日 上午10:06:21
 */
public class MySqlOperater implements DatabaseOperater, Closeable {

	private final String FETCH_DBS_SQL = "SELECT SCHEMA_NAME NAME FROM INFORMATION_SCHEMA.SCHEMATA ORDER BY SCHEMA_NAME";
	private final String FETCH_TABLES_SQL = "SELECT T.TABLE_NAME AS NAME, T.TABLE_COMMENT AS COMMENTS FROM INFORMATION_SCHEMA.`TABLES` T  WHERE T.TABLE_SCHEMA = (SELECT DATABASE())";
	private final String FETCH_TABLE_COLUMNS_SQL = "SELECT T.COLUMN_NAME AS NAME, (CASE WHEN T.COLUMN_KEY = 'PRI' THEN '1' ELSE '0' END) AS IS_PK, " + 
			"         (CASE WHEN T.IS_NULLABLE = 'YES' THEN '1' ELSE '0' END) AS IS_DB_NULL, " + 
			"		 (T.ORDINAL_POSITION * 10) AS SORT, T.COLUMN_COMMENT AS COMMENTS, T.COLUMN_TYPE AS DB_TYPE, " + 
			"		 (CASE WHEN T.CHARACTER_MAXIMUM_LENGTH IS NULL THEN T.NUMERIC_PRECISION ELSE T.CHARACTER_MAXIMUM_LENGTH END) NUMERIC_PRECISION, " + 
			"		 T.NUMERIC_SCALE " + 
			"    FROM INFORMATION_SCHEMA.COLUMNS T " + 
			"   WHERE T.TABLE_SCHEMA = (SELECT DATABASE()) " + 
			"     AND T.TABLE_NAME = UPPER('%s')";
	private final String URL_TEMPLATE = "jdbc:mysql://%s:%s";
	private String ip;
	private String port;
	private String url;
	private String userName;
	private String passWord;
	private Connection conn;
	private AtomicBoolean opened = new AtomicBoolean(false);

	public MySqlOperater(String ip, String port, String userName, String passWord) {
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
	public Table getTable(String db, String table) {
		if (!this.opened.get()) {
			this.open();
		}
		this.execSql(conn, "USE " + db);
		List<TableColumn> columns = this.execSql(conn, StringUtils.format(FETCH_TABLE_COLUMNS_SQL, table), (rs, sn) -> {
			TableColumn column = new TableColumn();
			column.setName(rs.getString("NAME"));
			column.setDbType(rs.getString("DB_TYPE"));
			column.setIsNull(rs.getByte("IS_DB_NULL"));
			column.setIsPk(rs.getByte("IS_PK"));
			column.setComments(rs.getString("COMMENTS"));
			return column;
		});
		Table _table = new Table();
		_table.setName(table);
		_table.setColumns(columns);
		return _table;
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
