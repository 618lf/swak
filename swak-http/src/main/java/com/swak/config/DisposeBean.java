package com.swak.config;

import org.springframework.beans.factory.DisposableBean;

import com.swak.common.cache.redis.RedisUtils;
import com.swak.common.http.HttpClients;
import com.swak.common.persistence.DataSourceHolder;
import com.swak.common.persistence.JdbcSqlExecutor;
import com.swak.common.serializer.SerializationUtils;
import com.swak.common.utils.SpringContextHolder;
import com.swak.security.session.SessionProvider;

/**
 * 释放系统引用的一些资源
 * @author lifeng
 */
public class DisposeBean implements DisposableBean{

	@Override
	public void destroy() throws Exception {
		SpringContextHolder.setApplicationContext(null);
		RedisUtils.setRedisConnectionFactory(null);
		SerializationUtils.g_ser = null;
		JdbcSqlExecutor.setJdbcTemplate(null);
		DataSourceHolder.setDataSource(null);
		SessionProvider.setRepository(null);
		HttpClients.setAsyncHttpClient(null);
	}
}
