package com.swak.persistence.async;

import java.sql.SQLException;

import io.vertx.sqlclient.Row;

/**
 * 行处理
 * 
 * @author lifeng
 * @date 2020年9月30日 下午8:47:41
 */
public interface RowMapper<T> {

	T mapRow(Row rs, int rowNum) throws SQLException;
}
