package com.swak.persistence.dialect;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * mssql 的分页
 * 
 * @author lifeng
 */
public class MsSQLDialect implements Dialect {

	protected static final String SQL_END_DELIMITER = ";";

	public String getLimitString(String sql, int offset, int limit) {
		sql = trim(sql);
		StringBuffer sb = new StringBuffer(sql.length() + 64);
		sb.append(sql).append(" OFFSET ").append(offset).append(" ROWS FETCH NEXT ").append(limit).append(" ROWS ONLY")
				.append(SQL_END_DELIMITER);
		return sb.toString();
	}

	private String trim(String sql) {
		sql = sql.trim();
		if (sql.endsWith(SQL_END_DELIMITER)) {
			sql = sql.substring(0, sql.length() - 1 - SQL_END_DELIMITER.length());
		}
		return sql;
	}

	@Override
	public Map<String, String> variables() {
		Map<String, String> variables = Maps.newHashMap();
		variables.put("X_LEN", "LEN");
		return variables;
	}

	@Override
	public boolean supportsLimit() {
		return true;
	}

	@Override
	public String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer(sql.length() + 20).append(trim(sql)).append(hasOffset ? " LIMIT ?,?" : " LIMIT ?")
				.append(SQL_END_DELIMITER).toString();
	}
}