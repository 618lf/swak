package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.swak.common.Constants;
import com.swak.common.persistence.JdbcSqlExecutor;
import com.swak.common.persistence.QueryCondition;
import com.swak.common.persistence.dialect.MySQLDialect;
import com.swak.common.persistence.mybatis.ExecutorInterceptor;
import com.swak.common.utils.StringUtils;
import com.swak.config.database.ConfigurationCustomizer;
import com.swak.config.database.DataSourceProperties;
import com.swak.config.database.DruidDataSourceAutoConfiguration;
import com.swak.config.database.HikariDataSourceAutoConfiguration;
import com.swak.config.database.MybatisProperties;
import com.swak.config.database.SpringBootVFS;

/**
 * 数据库相关配置
 * 
 * @author lifeng
 */
public class DataBaseConfigurationSupport {

	/**
	 * Druid 数据源
	 * 
	 * @author lifeng
	 */
	@org.springframework.context.annotation.Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnClass(DataSource.class)
	@ConditionalOnMissingBean(DataSource.class)
	@EnableConfigurationProperties(DataSourceProperties.class)
	@Import({
		DruidDataSourceAutoConfiguration.class,
		HikariDataSourceAutoConfiguration.class
	})
	public static class DataSourceAutoConfiguration {}

	/**
	 * JDBC 操作模板
	 * 
	 * @author lifeng
	 */
	@org.springframework.context.annotation.Configuration
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class })
	@ConditionalOnSingleCandidate(DataSource.class)
	@AutoConfigureAfter(DataSourceAutoConfiguration.class)
	@EnableConfigurationProperties(JdbcProperties.class)
	public static class JdbcTemplateAutoConfiguration {

		@org.springframework.context.annotation.Configuration
		static class JdbcTemplateConfiguration {

			private final DataSource dataSource;

			private final JdbcProperties properties;

			JdbcTemplateConfiguration(DataSource dataSource, JdbcProperties properties) {
				APP_LOGGER.debug("Loading Jdbc Template");
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

	/**
	 * 数据库事务 -- 使用spring boot 的配置
	 * 
	 * @author lifeng
	 */
	@org.springframework.context.annotation.Configuration
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnClass({ JdbcTemplate.class, PlatformTransactionManager.class })
	@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
	@ConditionalOnSingleCandidate(DataSource.class)
	@Import({ DataSourceTransactionManagerAutoConfiguration.class, TransactionAutoConfiguration.class })
	public static class DataSourceTransactionManagerConfiguration {
		DataSourceTransactionManagerConfiguration() {
			APP_LOGGER.debug("Loading Transaction Manager");
		}
	}

	/**
	 * Mybatis
	 * 
	 * @author lifeng
	 *
	 */
	@org.springframework.context.annotation.Configuration
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
	@ConditionalOnBean(DataSource.class)
	@EnableConfigurationProperties(MybatisProperties.class)
	@AutoConfigureAfter(DataSourceAutoConfiguration.class)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableMybatis", matchIfMissing = true)
	public static class MybatisAutoConfiguration {
		private final MybatisProperties properties;
		private final Interceptor[] interceptors;
		private final ResourceLoader resourceLoader;
		private final DatabaseIdProvider databaseIdProvider;
		private final List<ConfigurationCustomizer> configurationCustomizers;
		
		public MybatisAutoConfiguration(MybatisProperties properties,
				ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader,
				ObjectProvider<DatabaseIdProvider> databaseIdProvider,
				ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
			APP_LOGGER.debug("Loading Mybatis");
			this.properties = properties;
			this.interceptors = interceptorsProvider.getIfAvailable();
			this.resourceLoader = resourceLoader;
			this.databaseIdProvider = databaseIdProvider.getIfAvailable();
			this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
		}

		@PostConstruct
		public void checkConfigFileExists() {
			if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
				Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
				Assert.state(resource.exists(), "Cannot find config location: " + resource
						+ " (please add config file or check your Mybatis configuration)");
			}
		}

		@Bean
		@ConditionalOnMissingBean
		public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(dataSource);
			factory.setVfs(SpringBootVFS.class);
			if (StringUtils.hasText(this.properties.getConfigLocation())) {
				factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
			}
			Configuration configuration = this.properties.getConfiguration();
			if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
				configuration = new Configuration();
			}
			if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
				for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
					customizer.customize(configuration);
				}
			}
			factory.setConfiguration(configuration);
			if (this.properties.getConfigurationProperties() != null) {
				factory.setConfigurationProperties(this.properties.getConfigurationProperties());
			}
			if (!ObjectUtils.isEmpty(this.interceptors)) {
				factory.setPlugins(this.interceptors);
			}
			if (this.databaseIdProvider != null) {
				factory.setDatabaseIdProvider(this.databaseIdProvider);
			}
			if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
				factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
			}
			if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
				factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
			}
			if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
				factory.setMapperLocations(this.properties.resolveMapperLocations());
			}

			// 默认配置
			this.defaultConfiguration(configuration);
			return factory.getObject();
		}

		private void defaultConfiguration(Configuration configuration) {

			// 默认的拦截器
			ExecutorInterceptor interceptor = new ExecutorInterceptor();
			interceptor.setDialect(new MySQLDialect());
			configuration.addInterceptor(interceptor);

			// 默认的别名
			configuration.getTypeAliasRegistry().registerAlias("queryCondition", QueryCondition.class);
		}

		@Bean
		@ConditionalOnMissingBean
		public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
			ExecutorType executorType = this.properties.getExecutorType();
			if (executorType != null) {
				return new SqlSessionTemplate(sqlSessionFactory, executorType);
			} else {
				return new SqlSessionTemplate(sqlSessionFactory);
			}
		}
	}
}
