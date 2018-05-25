package com.swak.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * JDBC sql 简单的执行器,依赖外部的事务
 * @author lifeng
 */
public class JdbcSqlExecutor {
 
	private static NamedParameterJdbcTemplate jdbcTemplate = null;
	
	/**
	 * 设置 jdbcTemplate
	 * @param jdbcTemplate
	 * @return
	 */
	public static void setJdbcTemplate(NamedParameterJdbcTemplate _jdbcTemplate) { 
		jdbcTemplate = _jdbcTemplate;
	}
	
	// ---- 提供一些简单的方法来执行sql
	/**
	 * 插入数据
	 * @param sql
	 * @param param
	 */
	public static void insert(String sql, Map<String, ?> param) {
		jdbcTemplate.update(sql, param);
	}
	
	/**
	 * 更新数据
	 * @param sql
	 * @param param
	 */
	public static void update(String sql, Map<String, ?> param) {
		jdbcTemplate.update(sql, param);
	}
	
	/**
	 * 执行一条SQL
	 * @param sql
	 */
	public static void delete(String sql, Map<String, ?> param) {
		jdbcTemplate.update(sql, param);
	}
	
	/**
	 * 结果
	 * @param sql
	 * @param param
	 * @param rowMapper
	 * @return
	 */
	public static <T> List<T> query(String sql, Map<String, ?> param, RowMapper<T> rowMapper) {
		return jdbcTemplate.query(sql, param, rowMapper);
	}
	
	/**
	 * 数量
	 * @param sql
	 * @param param
	 * @return
	 */
	public static Integer count(String sql, Map<String, ?> param) {
		return jdbcTemplate.queryForObject(sql, param, Integer.class);
	}
}