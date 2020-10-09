package com.swak.async.persistence.define;

import java.util.List;

/**
 * 主键定义
 * 
 * @author lifeng
 * @date 2020年10月8日 上午12:12:59
 */
public class PkDefine {

	/**
	 * 单主键
	 */
	public ColumnDefine single;

	/**
	 * 多主键
	 */
	public List<ColumnDefine> columns;
}
