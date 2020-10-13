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
public class DeleteSql<T> extends BaseSql<T> implements Dml<T> {

	public DeleteSql(TableDefine<T> table) {
		super(table);
	}

	@Override
	public String parseScript(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE).append(SPACE).append(FROM).append(SPACE).append(TABLE).append(SPACE)
				.append(this.parseTable()).append(SPACE);
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());
		return sql.toString();
	}

	@Override
	public <U> RowMapper<U> rowMap() {
		return null;
	}
}