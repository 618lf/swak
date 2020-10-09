package com.swak.async.persistence.define;

import com.swak.async.persistence.RowMapper;

/**
 * Map 映射
 * 
 * @author lifeng
 * @date 2020年10月8日 下午6:43:07
 */
public class MapDefine<T> extends NameDefine {

	/**
	 * 映射 处理器
	 */
	public RowMapper<T> mapper;

	public MapDefine(String name, RowMapper<T> mapper) {
		this.name = name;
		this.mapper = mapper;
	}
}
