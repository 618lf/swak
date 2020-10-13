package com.swak.jdbc;

import com.swak.async.persistence.BaseDao;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.sqls.ExecuteSql;
import com.swak.persistence.QueryCondition;

/**
 * 异步操作
 * 
 * @author lifeng
 * @date 2020年10月3日 下午5:57:56
 */
public class UserDao extends BaseDao<User, Long> {

	@Override
	public String toString() {
//		User user = new User();
//		user.setId(12L);
		QueryCondition query = new QueryCondition();
		query.getCriteria().andEqualTo("ID", 12L);
		StringBuilder s = new StringBuilder();
		this.sqlMap.sqls.forEach((name, sql) -> {
			ExecuteSql<User> executeSql = (ExecuteSql<User>) sql;
			SqlParam<User> param = sql.newParam().setQuery(query);
			String sqlString = executeSql.parseScript(param);
			System.out.println(sqlString);
		});
		return s.toString();
	}
}
