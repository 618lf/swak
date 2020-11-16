package com.swak.config.jdbc;

import static com.swak.Application.APP_LOGGER;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.swak.Constants;
import com.swak.config.jdbc.database.DataSourceProperties;
import com.swak.config.jdbc.database.DruidDataSourceAutoConfiguration;
import com.swak.config.jdbc.database.HikariDataSourceAutoConfiguration;
import com.swak.config.jdbc.database.SqlLiteDataSourceAutoConfiguration;
import com.swak.config.jdbc.sharding.ShardingJdbcConfiguration;
import com.swak.persistence.JDBCDrivers;
import com.swak.persistence.dialect.Dialect;

/**
 * JDBC 操作模板
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(JdbcTemplate.class)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, ShardingJdbcConfiguration.class,
		SqlLiteDataSourceAutoConfiguration.class, DruidDataSourceAutoConfiguration.class,
		HikariDataSourceAutoConfiguration.class })
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableJdbc", matchIfMissing = true)
public class JdbcTemplateAutoConfiguration {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private DataSourceProperties dbProperties;

	public JdbcTemplateAutoConfiguration() {
		APP_LOGGER.debug("Loading Jdbc");
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean(JdbcOperations.class)
	public JdbcTemplate jdbcTemplate() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		jdbcTemplate.setFetchSize(dbProperties.getJdbcFetchSize());
		jdbcTemplate.setMaxRows(dbProperties.getJdbcMaxRows());
		return jdbcTemplate;
	}

	@Bean
	@Primary
	@ConditionalOnSingleCandidate(JdbcTemplate.class)
	@ConditionalOnMissingBean(NamedParameterJdbcOperations.class)
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
		return template;
	}

	@Bean
	@ConditionalOnMissingBean(Dialect.class)
	public Dialect dbDialect() {
		return JDBCDrivers.getDialect(this.dbProperties.getUrl(), this.dbProperties.getDriverClassName());
	}
}
