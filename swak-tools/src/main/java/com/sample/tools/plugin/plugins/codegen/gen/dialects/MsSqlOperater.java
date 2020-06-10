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
 * mssql 的表操作
 * 
 * @author lifeng
 * @date 2020年6月4日 下午9:44:34
 */
public class MsSqlOperater implements DatabaseOperater, Closeable {

	private final String FETCH_DBS_SQL = "SELECT NAME FROM SYSDATABASES WHERE DBID>4 ORDER BY NAME";
	private final String FETCH_TABLES_SQL = "SELECT NAME FROM SYSOBJECTS WHERE (XTYPE='U' OR XTYPE='V') ORDER BY NAME";
	private final String FETCH_TABLE_COLUMNS_SQL = "SELECT C.NAME AS [COLUMN_NAME], T.NAME AS [DATA_TYPE], C.MAX_LENGTH AS [MAX_LENGTH],  C.IS_NULLABLE AS [IS_NULLABLE], ISNULL(I.IS_PRIMARY_KEY, 0) [PRIMARY_KEY], I.TYPE_DESC FROM  SYS.COLUMNS C INNER JOIN SYS.TYPES T ON C.SYSTEM_TYPE_ID = T.SYSTEM_TYPE_ID AND C.USER_TYPE_ID = T.USER_TYPE_ID LEFT OUTER JOIN ( SYS.INDEX_COLUMNS IC INNER JOIN SYS.INDEXES I ON IC.OBJECT_ID = I.OBJECT_ID AND I.IS_PRIMARY_KEY = 1 AND IC.INDEX_ID = I.INDEX_ID  ) ON IC.OBJECT_ID = C.OBJECT_ID AND IC.COLUMN_ID = C.COLUMN_ID WHERE   C.OBJECT_ID = OBJECT_ID('%s')  ORDER BY C.COLUMN_ID";
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
	public Table getTable(String db, String table) {
		if (!this.opened.get()) {
			this.open();
		}
		this.execSql(conn, "USE " + db);
		List<TableColumn> columns = this.execSql(conn, StringUtils.format(FETCH_TABLE_COLUMNS_SQL, table), (rs, sn) -> {
			TableColumn column = new TableColumn();
			column.setName(rs.getString("COLUMN_NAME"));
			column.setDbType(rs.getString("DATA_TYPE"));
			column.setIsNull(rs.getByte("IS_NULLABLE"));
			column.setIsPk(rs.getByte("PRIMARY_KEY"));
			column.setComments(rs.getString("TYPE_DESC"));
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