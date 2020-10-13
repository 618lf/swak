package com.swak.async.persistence.sqls;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;

/**
 * 删除脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 下午4:28:10
 */
public class LockSql<T> extends BaseSql<T> implements Dml<T> {

	RowMapper<T> map;

	public LockSql(TableDefine<T> table, RowMapper<T> map) {
		super(table);
		this.map = map;
	}

	@Override
	public String parseScript(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(this.parseColumns()).append(FROM).append(SPACE)
				.append(this.parseTable()).append(SPACE).append(WHERE).append(SPACE).append(this.parseEqualsIdParams())
				.append(SPACE).append(LOCK);
		return sql.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> RowMapper<U> rowMap() {
		return (RowMapper<U>) map;
	}
}