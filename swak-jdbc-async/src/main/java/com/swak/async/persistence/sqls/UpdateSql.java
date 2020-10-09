package com.swak.async.persistence.sqls;

import com.swak.async.persistence.define.TableDefine;

/**
 * 添加脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 上午12:05:05
 */
public class UpdateSql<T> extends BaseSql<T> {

	public UpdateSql(TableDefine<T> table) {
		super(table);
	}

	@Override
	protected String parseScript() {
		// SQL 脚本
		StringBuilder sql = new StringBuilder();
		sql.append(UPDATE).append(SPACE).append(this.parseTable()).append(SPACE).append(SET).append(SPACE);
		sql.append(this.parseUpdateParams());
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());
		return sql.toString();
	}
}
