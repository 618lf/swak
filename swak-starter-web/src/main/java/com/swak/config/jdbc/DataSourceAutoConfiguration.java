package com.swak.config.jdbc;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.swak.config.jdbc.database.DataSourceProperties;
import com.swak.config.jdbc.database.DruidDataSourceAutoConfiguration;
import com.swak.config.jdbc.database.HikariDataSourceAutoConfiguration;
import com.swak.config.jdbc.database.SqlLiteDataSourceAutoConfiguration;
import com.swak.config.jdbc.sharding.ShardingJdbcConfiguration;

/**
 * DataSource 数据源
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(JdbcTemplate.class)
@ConditionalOnMissingBean(DataSource.class)
@EnableConfigurationProperties(DataSourceProperties.class)
@Import({ ShardingJdbcConfiguration.class, SqlLiteDataSourceAutoConfiguration.class,
		DruidDataSourceAutoConfiguration.class, HikariDataSourceAutoConfiguration.class })
public class DataSourceAutoConfiguration {

}
