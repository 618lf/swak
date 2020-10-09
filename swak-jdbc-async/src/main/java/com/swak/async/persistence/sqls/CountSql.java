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
public class CountSql<T> extends BaseSql<T> {

	RowMapper<Integer> map;

	public CountSql(TableDefine<T> table, RowMapper<Integer> map) {
		super(table);
		this.map = map;
	}

	@Override
	protected String parseScript() {

		// sql 语句
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(COUNT).append(SPACE).append(FROM).append(SPACE)
				.append(this.parseTable()).append(SPACE);

		// 返回Sql 语句
		return sql.toString();
	}

	/**
	 * 编译查询条件
	 * 
	 * @param qc
	 * @return
	 */
	public String parseScriptWithCondition(QueryCondition qc) {

		// 解析Sql
		String sql = this.parseScript();

		// 如果已经设置了 WHERE
		if (sql.endsWith("WHERE")) {
			sql = new StringBuilder(sql).append(" 1=1 ").append(qc.toString()).toString();
		} else {
			sql = new StringBuilder(sql).append(" WHERE 1=1 ").append(qc.toString()).toString();
		}

		// 返回查询
		return sql;
	}

	/**
	 * 获取映射
	 * 
	 * @return
	 */
	public RowMapper<Integer> getMap() {
		return map;
	}
}
