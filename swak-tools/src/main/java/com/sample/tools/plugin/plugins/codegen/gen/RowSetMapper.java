package com.sample.tools.plugin.plugins.codegen.gen;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Row 遍历
 * 
 * @author lifeng
 * @date 2020年6月5日 上午11:12:38
 */
public interface RowSetMapper<T> {

	/**
	 * 处理数据
	 * 
	 * @param rs
	 * @param rowNum
	 * @return
	 * @throws SQLException
	 */
	T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
