package com.swak.jdbc;

import com.swak.async.persistence.BaseDao;

/**
 * 异步操作
 * 
 * @author lifeng
 * @date 2020年10月3日 下午5:57:56
 */
public class UserDao extends BaseDao<User, Long> {

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		this.sqlMap.sqls.forEach((name, sql) -> {
		});
		return s.toString();
	}
}
