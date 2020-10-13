package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.define.TableDefine;

/**
 * 总数查询
 * 
 * @author lifeng
 * @date 2020年10月8日 下午7:49:16
 */
public class CountSql<T> extends ShardingSql<T> implements Dql<T> {

	RowMapper<Integer> map;

	public CountSql(TableDefine<T> table, RowMapper<Integer> map) {
		super(table);
		this.map = map;
	}

	@Override
	public String parseScript(SqlParam<T> param) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(COUNT).append(SPACE).append(FROM).append(SPACE)
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