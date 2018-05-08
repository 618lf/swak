package com.swak.common.persistence.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.common.persistence.dialect.Dialect;

/**
 * <ul>
 * <li>Title: Mybatis查询拦截器</li>
 * <li>Description: 拦截Executor 的 query 方法</li>
 * <li>Copyright: www.gzzyzz.com</li>
 * <li>Company:</li>
 * </ul>
 * @author Hill
 * @version 2014-10-13
 */
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class ExecutorInterceptor extends AbstractInterceptor {
	
	private final static Logger logger = LoggerFactory.getLogger(ExecutorInterceptor.class);
	private static int MAPPED_STATEMENT_INDEX = 0;
	private static int PARAMETER_INDEX = 1;
	private static int ROWBOUNDS_INDEX = 2;

	private Dialect dialect;

	/**
	 * 执行拦截方法，如果 RowBounds 设置了分页的参数来走分页插件
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		processIntercept(invocation.getArgs());
		return invocation.proceed();
	}

	private void processIntercept(final Object[] queryArgs) {
		final RowBounds rowBounds = (RowBounds) queryArgs[ROWBOUNDS_INDEX];
		int offset = rowBounds.getOffset();
		int limit = rowBounds.getLimit();
		//分页
		if((offset != RowBounds.NO_ROW_OFFSET || limit != RowBounds.NO_ROW_LIMIT) && dialect.supportsLimit()) {
		   MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
		   Object parameter = queryArgs[PARAMETER_INDEX];
		   BoundSql boundSql = ms.getBoundSql(parameter);
		   String sql = boundSql.getSql().replaceAll("\\s{2,}", " ").trim();
		   sql = dialect.getLimitString(sql, offset, limit);
		   offset = RowBounds.NO_ROW_OFFSET; limit = RowBounds.NO_ROW_LIMIT;
		   queryArgs[ROWBOUNDS_INDEX] = new RowBounds(offset, limit);
		   BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, sql);
		   MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
		   queryArgs[MAPPED_STATEMENT_INDEX] = newMs;
		   logger.debug("PAGE SQL:%s", sql);
		}
	}
	
	/**
	 * 是否拦截
	 */
	@Override
	public Object plugin(Object target) {  
	  if(target instanceof Executor) {
		 return Plugin.wrap(target, this);
	  }
      return target;
    }
	
	/**
	 * 初始化属性
	 */
	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}
}