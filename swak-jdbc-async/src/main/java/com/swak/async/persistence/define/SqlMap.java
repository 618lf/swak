package com.swak.async.persistence.define;

import java.util.Map;

import com.google.common.collect.Maps;
import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.Sql;
import com.swak.async.persistence.maps.CountMapper;
import com.swak.async.persistence.maps.ModelMapper;
import com.swak.async.persistence.maps.UpdateMapper;
import com.swak.async.persistence.sqls.CountSql;
import com.swak.async.persistence.sqls.DeleteSql;
import com.swak.async.persistence.sqls.ExistsSql;
import com.swak.async.persistence.sqls.GetSql;
import com.swak.async.persistence.sqls.InsertSql;
import com.swak.async.persistence.sqls.LockSql;
import com.swak.async.persistence.sqls.QuerySql;
import com.swak.async.persistence.sqls.UpdateSql;

/**
 * Sql 映射
 * 
 * @author lifeng
 * @date 2020年10月8日 下午6:13:05
 */
public class SqlMap<T> {

	/**
	 * 内置的语句
	 */
	public final static String EXISTS = "exists";
	public final static String LOCK = "lock";
	public final static String INSERT = "insert";
	public final static String UPDATE = "update";
	public final static String DELETE = "delete";
	public final static String GET = "get";
	public final static String QUERY = "query";
	public final static String COUNT = "queryStat";

	/**
	 * 表定义
	 */
	public TableDefine<T> table;

	/**
	 * Sql 脚本
	 */
	public Map<String, Sql<T>> sqls = Maps.newHashMap();

	/**
	 * 定义SqlMap
	 * 
	 * @param table
	 */
	public SqlMap(TableDefine<T> table) {

		// 表定义
		this.table = table;

		// 注册默认的Sql
		this.registerSqls();
	}

	/**
	 * 注册SQL语句
	 */
	public void registerSqls() {

		// 默认的映射
		RowMapper<T> modelMapper = new ModelMapper<>(this.table);
		RowMapper<Integer> countMapper = new CountMapper();
		RowMapper<Integer> updateMapper = new UpdateMapper();

		// 注册Sql
		this.sqls.put(EXISTS, new ExistsSql<T>(this.table, countMapper));
		this.sqls.put(LOCK, new LockSql<T>(this.table, modelMapper));
		this.sqls.put(INSERT, new InsertSql<T>(this.table, updateMapper));
		this.sqls.put(UPDATE, new UpdateSql<T>(this.table, updateMapper));
		this.sqls.put(DELETE, new DeleteSql<T>(this.table, updateMapper));
		this.sqls.put(GET, new GetSql<T>(this.table, modelMapper));
		this.sqls.put(QUERY, new QuerySql<T>(this.table, modelMapper));
		this.sqls.put(COUNT, new CountSql<T>(this.table, countMapper));
	}
}