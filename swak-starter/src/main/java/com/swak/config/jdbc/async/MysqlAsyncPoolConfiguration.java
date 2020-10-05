package com.swak.config.jdbc.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.jdbc.AsyncDataSourceProperties;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

/**
 * 支持
 * 
 * @author lifeng
 * @date 2020年9月30日 下午8:12:13
 */
@Configuration
@ConditionalOnClass(MySQLPool.class)
@EnableConfigurationProperties(AsyncDataSourceProperties.class)
public class MysqlAsyncPoolConfiguration {

	@Autowired
	AsyncDataSourceProperties properties;

	/**
	 * 异步操作池
	 * 
	 * @return
	 */
	@Bean
	public Pool asyncJdbcPool() {
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(properties.getPort())
				.setHost(properties.getHost()).setDatabase(properties.getDatabase()).setUser(properties.getUsername())
				.setPassword(properties.getPassword());

		// Pool options
		PoolOptions poolOptions = new PoolOptions().setMaxSize(properties.getMaxActive());

		// Create the client pool
		MySQLPool client = MySQLPool.pool(connectOptions, poolOptions);

		// 返回操作池
		return client;
	}

}