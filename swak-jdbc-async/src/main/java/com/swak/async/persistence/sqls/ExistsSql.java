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
public class ExistsSql<T> extends CountSql<T> {

	public ExistsSql(TableDefine<T> table, RowMapper<Integer> map, MetricsFactory metricsFactory) {
		super(table, map, metricsFactory);
	}

	@Override
	public String parseScript(SqlParam<T> param) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(COUNT).append(SPACE).append(FROM).append(SPACE)
				.append(this.parseTable(param)).append(SPACE);
		sql.append(WHERE).append(SPACE).append(this.parseEqualsIdParams());
		return this.parseQuery(sql.toString(), param.query);
	}

	/**
	 * 解析参数
	 */
	@Override
	public List<Object> parseParams(SqlParam<T> param) {
		return param.getEntityIdValues();
	}
}