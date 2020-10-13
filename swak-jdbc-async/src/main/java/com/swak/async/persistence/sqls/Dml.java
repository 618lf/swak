package com.swak.async.persistence.sqls;

import com.swak.async.persistence.Sql;

/**
 * 操作型Sql
 * 
 * @author lifeng
 * @date 2020年10月12日 上午10:48:00
 */
public interface Dml<T> extends Sql<T> {
	default boolean hasTransaction() {
		return true;
	}
}