package com.swak.persistence.mybatis;

import java.util.Properties;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;

/**
 * Mybatis拦截器基类
 * @author root
 */
public abstract class AbstractInterceptor implements Interceptor {
	
	/**
	 * 复制 MappedStatement
	 * @param ms
	 * @param newSqlSource
	 * @return
	 */
	protected MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		String[] s = ms.getKeyProperties();
		if (s == null) {
			builder.keyProperty(null);
		} else {
			builder.keyProperty(s[0]);
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.cache(ms.getCache());
		return builder.build();
	}
	
	/**
	 * 复制 BoundSql
	 * @param ms
	 * @param newSqlSource
	 * @return
	 */
	protected BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
		for(ParameterMapping mapping : boundSql.getParameterMappings()) {  
	        String prop = mapping.getProperty();  
	        if(boundSql.hasAdditionalParameter(prop)) {  
	           newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));  
	        }  
	    }  
	    return newBoundSql;  
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;
		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}
		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}
	
	@Override
	public Object plugin(Object target) {
		return target; 
	}
	
	@Override
	public void setProperties(Properties properties) {}
}
