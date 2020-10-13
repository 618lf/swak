package com.swak.async.persistence.sqls;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.swak.async.persistence.Sql;
import com.swak.async.persistence.define.ColumnDefine;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;
import com.swak.utils.Maps;

import net.sf.cglib.beans.BeanMap;

/**
 * 定义基础的语句块
 * 
 * @author lifeng
 * @date 2020年10月8日 下午3:03:20
 */
public abstract class BaseSql<T> extends ExecuteSql<T> implements Sql<T> {

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

	/**
	 * 表定义
	 */
	protected final TableDefine<T> table;

	/**
	 * 创建的基本的Sql
	 * 
	 * @param table
	 */
	public BaseSql(TableDefine<T> table) {
		this.table = table;
	}

	/**
	 * 通过cglib 高效的转换, 不能使用lombok的链式功能
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> BeantoMap(T bean) {
		Map<String, Object> map = Maps.newHashMap();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			Set<String> keys = beanMap.keySet();
			for (Object key : keys) {
				map.put(String.valueOf(key), beanMap.get(key));
			}
		}
		return map;
	}

	/**
	 * 解析表
	 */
	protected String parseTable(T entity, QueryCondition query) {
		StringBuilder sql = new StringBuilder();
		sql.append(this.table.name);
		return sql.toString();
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
}