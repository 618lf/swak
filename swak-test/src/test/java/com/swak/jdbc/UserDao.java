package com.swak.jdbc;

import com.swak.async.execute.SqlExecuter;
import com.swak.async.persistence.BaseDao;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.sqls.ExecuteSql;

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
			ExecuteSql<User> executeSql = (ExecuteSql<User>) sql;
			SqlParam<User> param = sql.newParam();
			String sqlString = executeSql.parseScript(param);
			s.append(sqlString).append("\n");
		});
		return s.toString();
	}

	void setSqlExecuter(SqlExecuter executer) {
		this.sqlExecuter = executer;
	}
}
