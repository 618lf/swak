package com.swak.async.persistence.sqls;

import java.sql.SQLException;
import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;

/**
 * 查询
 * 
 * @author lifeng
 * @date 2020年10月8日 下午6:41:36
 */
public class QuerySql<T> extends BaseSql<T> {

	protected RowMapper<T> map;

	public QuerySql(TableDefine<T> table, RowMapper<T> map) {
		super(table);
		this.map = map;
	}

	@Override
	protected String parseScript() {
		// sql 语句
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT).append(SPACE).append(this.parseColumns()).append(FROM).append(SPACE)
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

		// 排序条件
		if (StringUtils.isNotBlank(qc.getOrderByClause())) {
			sql = new StringBuilder(sql).append(" ORDER BY ").append(qc.getOrderByClause()).toString();
		}

		// 返回查询
		return sql;
	}

	/**
	 * 获得Mapper
	 * 
	 * @return
	 */
	public RowMapper<T> getMapper() {
		return this.map;
	}

	/**
	 * 行处理
	 * 
	 * @param rows
	 * @return
	 * @throws SQLException
	 */
	protected List<T> mapperRows(RowSet<Row> rows) throws SQLException {
		List<T> ts = Lists.newArrayList();
		RowIterator<Row> datas = rows.iterator();
		int rowNum = 0;
		while (datas.hasNext()) {
			Row row = datas.next();
			ts.add(map.mapRow(row, rowNum));
			rowNum++;
		}
		return ts;
	}
}
