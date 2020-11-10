package com.swak.config.jdbc.async;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.async.persistence.datasource.DataSource;
import com.swak.async.persistence.tx.TransactionalAspect;
import com.swak.config.customizer.AsyncDataSourceOptionsCustomizer;
import com.swak.config.jdbc.AsyncDataSourceProperties;
import com.swak.vertx.transport.VertxProxy;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

/**
 * 支持异步Sql
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
	@ConditionalOnMissingBean(DataSource.class)
	public DataSource asyncJdbcPool(VertxProxy vertx,
			ObjectProvider<List<AsyncDataSourceOptionsCustomizer>> customizersProvider) {
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(properties.getPort())
				.setHost(properties.getHost()).setDatabase(properties.getDatabase()).setUser(properties.getUsername())
				.setPassword(properties.getPassword());

		// Pool options
		PoolOptions poolOptions = new PoolOptions().setMaxSize(properties.getMaxActive());

		// Create the client pool
		MySQLPool client = MySQLPool.pool(vertx.me(), connectOptions, poolOptions);

		// create datasource
		DataSource dataSource = new DataSource(client);
		List<AsyncDataSourceOptionsCustomizer> customizers = customizersProvider != null
				? customizersProvider.getIfAvailable()
				: null;
		if (customizers != null && !customizers.isEmpty()) {
			for (AsyncDataSourceOptionsCustomizer customizer : customizers) {
				customizer.customize(dataSource);
			}
		}

		// Return pool
		return dataSource;
	}

	/**
	 * 启用声明式事务
	 * 
	 * @return
	 */
	@Bean
	public TransactionalAspect transactionalAspect() {
		return new TransactionalAspect();
	}
}