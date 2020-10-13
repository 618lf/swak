package com.swak.async.persistence.sqls;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;

/**
 * 总数查询
 * 
 * @author lifeng
 * @date 2020年10月8日 下午7:49:16
 */
public class CountSql<T> extends BaseSql<T> implements Dql<T> {

	RowMapper<Integer> map;

	public CountSql(TableDefine<T> table, RowMapper<Integer> map) {
		super(table);
		this.map = map;
	}

	@Override
	public String parseScript(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(COUNT).append(SPACE).append(FROM).append(SPACE)
				.append(this.parseTable()).append(SPACE);
		return sql.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> RowMapper<U> rowMap() {
		return (RowMapper<U>) map;
	}
}