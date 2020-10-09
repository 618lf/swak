package com.swak.async.persistence;

import java.util.List;

/**
 * SQL 语句
 * 
 * @author lifeng
 * @date 2020年10月7日 下午11:30:33
 */
public interface Sql<T> {

	/**
	 * 返回SQL脚本: insert into table(c1, c2, c3) values(:c1, :c2, :c3);
	 * 
	 * @return
	 */
	String script();

	/**
	 * 解析参数：按照参数顺序排列
	 * 
	 * @return
	 */
	List<Object> parse(T entity);
}
