package com.swak.config.jdbc;

import static com.swak.Application.APP_LOGGER;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.swak.config.jdbc.database.DruidDataSourceAutoConfiguration;
import com.swak.config.jdbc.database.HikariDataSourceAutoConfiguration;
import com.swak.config.jdbc.database.SqlLiteDataSourceAutoConfiguration;
import com.swak.config.jdbc.sharding.ShardingJdbcConfiguration;
import com.swak.config.jdbc.transaction.DataSourceTransactionManagerAutoConfiguration;
import com.swak.config.jdbc.transaction.TransactionAutoConfiguration;

/**
 * 数据库事务
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ JdbcTemplate.class, PlatformTransactionManager.class })
@ConditionalOnSingleCandidate(DataSource.class)
@Import({ DataSourceTransactionManagerAutoConfiguration.class, TransactionAutoConfiguration.class })
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, ShardingJdbcConfiguration.class,
		SqlLiteDataSourceAutoConfiguration.class, DruidDataSourceAutoConfiguration.class,
		HikariDataSourceAutoConfiguration.class })
public class DataSourceTransactionManagerConfiguration {
	public DataSourceTransactionManagerConfiguration() {
		APP_LOGGER.debug("Loading Transaction Manager");
	}
}
