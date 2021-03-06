package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.Sql;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.define.ColumnDefine;
import com.swak.async.persistence.define.TableDefine;
import com.swak.async.persistence.sharding.ShardingStrategys;
import com.swak.meters.MetricsFactory;
import com.swak.persistence.QueryCondition;
import com.swak.utils.StringUtils;

/**
 * 定义基础的语句块
 * 
 * @author lifeng
 * @date 2020年10月8日 下午3:03:20
 */
public abstract class ShardingSql<T> extends ExecuteSql<T> implements Sql<T> {

	/**
	 * SQL 关键字
	 */
	protected static final String SELECT = "SELECT";
	protected static final String INSERT = "INSERT INTO";
	protected static final String UPDATE = "UPDATE";
	protected static final String DELETE = "DELETE";
	protected static final String FROM = "FROM";
	protected static final String TABLE = "TABLE";
	protected static final String SET = "SET";
	protected static final String WHERE = "WHERE";
	protected static final String EQUALS = " = ";
	protected static final String OCCUPIED = "?";
	protected static final String SPACE = " ";
	protected static final String SPLIT = ", ";
	protected static final String AND = " AND ";
	protected static final String LEFT_KH = "(";
	protected static final String RIGHT_KH = ")";
	protected static final String VALUES = "VALUES";
	protected static final String COUNT = "COUNT(1) C";
	protected static final String LOCK = "FOR UPDATE";
	protected static final String PADDING = "1=1";

	/**
	 * 表定义
	 */
	protected final TableDefine<T> table;

	/**
	 * 创建的基本的Sql
	 * 
	 * @param table
	 */
	public ShardingSql(TableDefine<T> table, MetricsFactory metricsFactory) {
		super(metricsFactory);
		this.table = table;
	}

	/**
	 * 解析条件
	 */
	protected String parseQuery(String sql, QueryCondition query) {
		String queryCondition = query == null ? StringUtils.EMPTY : query.toString();
		if (StringUtils.isNotBlank(queryCondition) && StringUtils.contains(sql, WHERE)) {
			return new StringBuilder(sql).append(SPACE).append(AND).append(SPACE).append(PADDING).append(SPACE)
					.append(queryCondition).toString();
		} else if (StringUtils.isNotBlank(queryCondition)) {
			return new StringBuilder(sql).append(WHERE).append(SPACE).append(queryCondition).toString();
		}
		return sql;
	}

	/**
	 * 解析列
	 */
	protected String parseColumns() {
		StringBuilder sql = new StringBuilder();
		for (ColumnDefine column : this.table.columns) {
			String name = column.name;
			sql.append(name).append(SPLIT);
		}
		if (this.table.hasColumn()) {
			sql.delete(sql.lastIndexOf(SPLIT), sql.length() - 1);
		}
		return sql.toString();
	}

	/**
	 * 解析插入列
	 */
	protected String parseEqualsIdParams() {
		StringBuilder sql = new StringBuilder();
		List<ColumnDefine> pks = this.table.getPkColumns();
		for (ColumnDefine column : pks) {
			sql.append(column.name).append(EQUALS).append(OCCUPIED).append(AND);
		}
		sql.delete(sql.lastIndexOf(AND), sql.length() - 1);
		return sql.toString();
	}

	/**
	 * 创建基本的参数
	 */
	@Override
	public SqlParam<T> newParam() {
		return new SqlParam<T>().setTable(table);
	}

	/**
	 * 解析表
	 */
	protected String parseTable(SqlParam<T> param) {
		return ShardingStrategys.shardingTable(param);
	}
}