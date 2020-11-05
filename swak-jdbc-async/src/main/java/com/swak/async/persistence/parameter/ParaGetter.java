package com.swak.async.persistence.parameter;

import com.swak.async.persistence.define.ColumnDefine;

import io.vertx.sqlclient.Row;

/**
 * 属性设置和获取
 * 
 * @author lifeng
 * @date 2020年10月14日 下午10:25:57
 */
public interface ParaGetter {

	/**
	 * 支持的类型
	 * 
	 * @param fieldClass
	 * @return
	 */
	boolean support(Class<?> fieldClass);

	/**
	 * 转换为Java类型
	 * 
	 * @param rs
	 * @param column
	 * @return
	 */
	Object toJava(Row rs, ColumnDefine column);

	/**
	 * 转换为Jdbc类型
	 * 
	 * @param java
	 * @return
	 */
	Object toJdbc(Object java);
}
