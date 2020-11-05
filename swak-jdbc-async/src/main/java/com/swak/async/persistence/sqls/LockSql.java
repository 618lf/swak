package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.define.TableDefine;
import com.swak.meters.MetricsFactory;

/**
 * 删除脚本
 * 
 * @author lifeng
 * @date 2020年10月8日 下午4:28:10
 */
public class LockSql<T> extends ShardingSql<T> implements Dml<T> {

	RowMapper<T> map;

	public LockSql(TableDefine<T> table, RowMapper<T> map, MetricsFactory metricsFactory) {
		super(table, metricsFactory);
		this.map = map;
	}

	@Override
	public String parseScript(SqlParam<T> param) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(this.parseColumns()).append(FROM).append(SPACE)
				.append(this.parseTable(param)).append(SPACE).append(WHERE).append(SPACE)
				.append(this.parseEqualsIdParams()).append(SPACE).append(LOCK);
		return sql.toString();
	}

	/**
	 * 解析参数
	 */
	@Override
	public List<Object> parseParams(SqlParam<T> param) {
		return param.getEntityIdValues();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> RowMapper<U> rowMap() {
		return (RowMapper<U>) map;
	}
}