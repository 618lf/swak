package com.swak.config.jdbc;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.swak.config.jdbc.database.DruidDataSourceAutoConfiguration;
import com.swak.config.jdbc.database.HikariDataSourceAutoConfiguration;
import com.swak.config.jdbc.database.SqlLiteDataSourceAutoConfiguration;
import com.swak.config.jdbc.sharding.ShardingJdbcConfiguration;
import com.swak.persistence.JdbcSqlExecutor;

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
	@org.springframework.context.annotation.Configuration
	static class JdbcTemplateConfiguration {

		private final DataSource dataSource;

		private final JdbcProperties properties;

		JdbcTemplateConfiguration(DataSource dataSource, JdbcProperties properties) {
			this.dataSource = dataSource;
			this.properties = properties;
		}

		@Bean
		@Primary
		@ConditionalOnMissingBean(JdbcOperations.class)
		public JdbcTemplate jdbcTemplate() {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
			JdbcProperties.Template template = this.properties.getTemplate();
			jdbcTemplate.setFetchSize(template.getFetchSize());
			jdbcTemplate.setMaxRows(template.getMaxRows());
			if (template.getQueryTimeout() != null) {
				jdbcTemplate.setQueryTimeout((int) template.getQueryTimeout().getSeconds());
			}
			return jdbcTemplate;
		}
	}

	@org.springframework.context.annotation.Configuration
	@Import(JdbcTemplateConfiguration.class)
	static class NamedParameterJdbcTemplateConfiguration {

		@Bean
		@Primary
		@ConditionalOnSingleCandidate(JdbcTemplate.class)
		@ConditionalOnMissingBean(NamedParameterJdbcOperations.class)
		public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
			NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
			JdbcSqlExecutor.setJdbcTemplate(template);
			return template;
		}
	}
}
