package com.swak.config.jdbc;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.swak.Constants;
import com.swak.async.persistence.datasource.DataSource;
import com.swak.async.persistence.execute.SqlExecuter;
import com.swak.config.jdbc.async.MetricsConfiguration;
import com.swak.config.jdbc.async.MysqlAsyncPoolConfiguration;

import io.vertx.sqlclient.SqlClient;

/**
 * 开启异步JDBC
 * 
 * @author lifeng
 * @date 2020年9月30日 下午8:02:36
 */
@Configuration
@ConditionalOnClass(SqlClient.class)
@EnableConfigurationProperties(AsyncDataSourceProperties.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableAsyncJdbc", matchIfMissing = true)
@Import(MysqlAsyncPoolConfiguration.class)
public class AsyncJdbcTemplateAutoConfiguration extends MetricsConfiguration{

	public AsyncJdbcTemplateAutoConfiguration() {
		APP_LOGGER.debug("Loading AsyncJdbc");
	}

	/**
	 * Sql执行器
	 * 
	 * @param pool
	 * @return
	 */
	@Bean
	public SqlExecuter asyncSqlExecuter(DataSource dataSource) {
		return new SqlExecuter(dataSource);
	}
}