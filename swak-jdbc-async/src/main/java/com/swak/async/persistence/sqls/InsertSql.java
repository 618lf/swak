package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.define.TableDefine;

/**
 * 插入SQL
 * 
 * @author lifeng
 * @date 2020年10月7日 下午11:31:12
 */
public class InsertSql<T> extends ShardingSql<T> implements Dml<T> {

	public InsertSql(TableDefine<T> table) {
		super(table);
	}

	@Override
	public String parseScript(SqlParam<T> param) {
		StringBuilder sql = new StringBuilder();
		sql.append(INSERT).append(SPACE).append(this.parseTable(param)).append(LEFT_KH);
		sql.append(this.parseColumns()).append(RIGHT_KH);
		sql.append(VALUES).append(LEFT_KH).append(this.parseInsertParams()).append(RIGHT_KH);
		return sql.toString();
	}

	protected String parseInsertParams() {
		StringBuilder sql = new StringBuilder();
		for (int i = 0; i < this.table.columns.size(); i++) {
			sql.append(OCCUPIED).append(SPLIT);
		}
		if (this.table.hasColumn()) {
			sql.delete(sql.lastIndexOf(SPLIT), sql.length() - 1);
		}
		return sql.toString();
	}

	@Override
	public List<Object> parseParams(SqlParam<T> param) {
		return param.getEntityValues();
	}

	@Override
	public <U> RowMapper<U> rowMap() {
		return null;
	}
}
