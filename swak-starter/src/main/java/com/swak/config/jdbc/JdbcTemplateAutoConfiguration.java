package com.swak.config.jdbc;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.swak.config.jdbc.database.DataSourceProperties;
import com.swak.config.jdbc.database.DruidDataSourceAutoConfiguration;
import com.swak.config.jdbc.database.HikariDataSourceAutoConfiguration;
import com.swak.config.jdbc.database.SqlLiteDataSourceAutoConfiguration;
import com.swak.config.jdbc.sharding.ShardingJdbcConfiguration;
import com.swak.persistence.Database;
import com.swak.persistence.dialect.Dialect;
import com.swak.persistence.dialect.H2Dialect;
import com.swak.persistence.dialect.MySQLDialect;
import com.swak.persistence.dialect.OracleDialect;
import com.swak.persistence.dialect.SqlLiteDialect;

/**
 * JDBC 操作模板
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(JdbcTemplate.class)
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, ShardingJdbcConfiguration.class,
		SqlLiteDataSourceAutoConfiguration.class, DruidDataSourceAutoConfiguration.class,
		HikariDataSourceAutoConfiguration.class })
@EnableConfigurationProperties(JdbcProperties.class)
public class JdbcTemplateAutoConfiguration {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private DataSourceProperties dbProperties;

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
	private Dialect dbDialect() {
		Database db = this.dbProperties.getDb();
		if (db == Database.h2) {
			return new H2Dialect();
		} else if (db == Database.mysql) {
			return new MySQLDialect();
		} else if (db == Database.oracle) {
			return new OracleDialect();
		} else if (db == Database.sqlite) {
			return new SqlLiteDialect();
		}
		return new MySQLDialect();
	}
}
