package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.define.TableDefine;

/**
 * 查询
 * 
 * @author lifeng
 * @date 2020年10月8日 下午6:41:36
 */
public class QuerySql<T> extends ShardingSql<T> implements Dql<T> {

	protected RowMapper<T> map;

	public QuerySql(TableDefine<T> table, RowMapper<T> map) {
		super(table);
		this.map = map;
	}

	@Override
	public String parseScript(SqlParam<T> param) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(this.parseColumns()).append(FROM).append(SPACE)
				.append(this.parseTable(param)).append(SPACE);
		return sql.toString();
	}

	@Override
	public List<Object> parseParams(SqlParam<T> param) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> RowMapper<U> rowMap() {
		return (RowMapper<U>) map;
	}
}
