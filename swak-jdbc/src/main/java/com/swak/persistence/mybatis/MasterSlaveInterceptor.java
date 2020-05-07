package com.swak.persistence.mybatis;

import java.util.Locale;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.swak.persistence.MS;
import com.swak.persistence.datasource.DataSourceHolder;

/**
 * 主从分离插件
 * 
 * @author lifeng
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }) })
public class MasterSlaveInterceptor implements Interceptor {

	/**
	 * Sql 匹配规则
	 */
	private static final String REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		// 是否是用事务管理
		boolean syschronizationActive = TransactionSynchronizationManager.isActualTransactionActive();

		// 默认走主数据源
		Object lookupKey = MS.Master;

		// 没有事务
		if (!syschronizationActive) {

			// 参数
			Object[] objects = invocation.getArgs();
			MappedStatement mappedStatement = (MappedStatement) objects[0];

			// 查询 -- 但是要排除： for update, 排除：SELECT LAST_INSERT_ID()；
			if (mappedStatement.getSqlCommandType().equals(SqlCommandType.SELECT)
					&& !mappedStatement.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {

				// 不是 for update
				BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(objects[1]);
				String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("[\\t\\n\\r]", " ");
				if (!sql.matches(REGEX)) {
					lookupKey = MS.Slave;
				}
			}
		}

		// 设置数据源
		DataSourceHolder.setDataSourceType(lookupKey);

		// 执行拦截
		try {
			return invocation.proceed();
		} finally {
			DataSourceHolder.clearDataSourceType();
		}
	}

	/**
	 * 设置拦截对象 Executor在mybatis中是用来增删改查的，进行拦截
	 *
	 * @param target 拦截的对象
	 * @return
	 */
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}
}