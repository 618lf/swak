package com.swak.async.persistence.sqls;

import com.swak.async.persistence.define.TableDefine;

/**
 * 删除脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 下午4:28:10
 */
public class DeleteSql<T> extends BaseSql<T> {

	public DeleteSql(TableDefine<T> table) {
		super(table);
	}

	@Override
	protected String parseScript() {

		// sql 语句
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE).append(SPACE).append(FROM).append(SPACE).append(TABLE).append(SPACE)
				.append(this.parseTable()).append(SPACE);

		// 拼接条件
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());

		// 返回Sql 语句
		return sql.toString();
	}
}