package com.swak.async.persistence.maps;

import java.sql.SQLException;

import com.swak.async.persistence.RowMapper;

import io.vertx.sqlclient.Row;

/**
 * 只是一个标识
 * 
 * @author lifeng
 * @date 2020年10月14日 下午9:24:33
 */
public class UpdateMapper implements RowMapper<Integer> {

	@Override
	public Integer mapRow(Row rs, int rowNum) throws SQLException {
		throw new RuntimeException("不支持获取数据！");
	}
}
