package com.swak.async.persistence.sqls;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;

/**
 * 添加脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 上午12:05:05
 */
public class UpdateSql<T> extends BaseSql<T> implements Dml<T> {

	public UpdateSql(TableDefine<T> table) {
		super(table);
	}

	@Override
	public String parseScript(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(UPDATE).append(SPACE).append(this.parseTable()).append(SPACE).append(SET).append(SPACE);
		sql.append(this.parseUpdateParams());
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());
		return sql.toString();
	}

	@Override
	public <U> RowMapper<U> rowMap() {
		return null;
	}
}
