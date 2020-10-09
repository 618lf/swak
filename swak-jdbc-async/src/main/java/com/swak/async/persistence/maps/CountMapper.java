package com.swak.async.persistence.maps;

import java.sql.SQLException;

import com.swak.async.persistence.RowMapper;

import io.vertx.sqlclient.Row;

/**
 * 获取总数
 * 
 * @author lifeng
 * @date 2020年10月8日 下午7:52:45
 */
public class CountMapper implements RowMapper<Integer> {

	@Override
	public Integer mapRow(Row rs, int rowNum) throws SQLException {
		return rs.getInteger("C");
	}
}