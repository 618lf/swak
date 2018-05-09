package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import java.sql.SQLException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.swak.common.Constants;
import com.swak.common.persistence.QueryCondition;
import com.swak.common.persistence.dialect.MySQLDialect;
import com.swak.common.persistence.mybatis.ExecutorInterceptor;
import com.swak.common.utils.StringUtils;
import com.swak.config.database.ConfigurationCustomizer;
import com.swak.config.database.DataSourceProperties;
import com.swak.config.database.MybatisProperties;
import com.swak.config.database.SpringBootVFS;

/**
 * 数据库相关配置
 * @author lifeng
 */
public class DataBaseConfigurationSupport {

	/**
	 * 数据源
	 * 
	 * @author lifeng
	 */
	@org.springframework.context.annotation.Configuration
	@ConditionalOnClass({DataSource.class, DruidDataSource.class})
	@EnableConfigurationProperties(DataSourceProperties.class)
	public static class DataSourceAutoConfiguration {
		
		@Autowired
		private DataSourceProperties properties;
		
		public DataSourceAutoConfiguration() {
			APP_LOGGER.debug("Loading Druid DataSource");
		}
		
		/**
		 * 如果还没有 DataSource 类型
		 * type = com.alibaba.druid.pool.DruidDataSource 就会构建这个dataSource
		 * @return
		 */
		@Bean
		public DataSource dataSource() {
			DruidDataSource dataSource = new DruidDataSource();
			dataSource.setUrl(properties.getUrl());
	        dataSource.setUsername(properties.getUsername());
	        dataSource.setPassword(properties.getPassword());

	        dataSource.setDriverClassName(properties.getDriverClassName());
	        dataSource.setInitialSize(properties.getInitialSize());     //定义初始连接数
	        dataSource.setMinIdle(properties.getMinIdle());             //最小空闲
	        dataSource.setMaxActive(properties.getMaxActive());         //定义最大连接数
	        dataSource.setMaxWait(properties.getMaxWait());             //最长等待时间

	        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
	        dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());

	        // 配置一个连接在池中最小生存的时间，单位是毫秒
	        dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
	        dataSource.setValidationQuery(properties.getValidationQuery());
	        dataSource.setTestWhileIdle(properties.getTestWhileIdle());
	        dataSource.setTestOnBorrow(properties.getTestOnBorrow());
	        dataSource.setTestOnReturn(properties.getTestOnReturn());

	        // 打开PSCache，并且指定每个连接上PSCache的大小
	        dataSource.setPoolPreparedStatements(properties.getPoolPreparedStatements());
	        dataSource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());

	        try {
	            dataSource.setFilters(properties.getFilters());
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
			return dataSource;
		}
	}
	
	/**
	 * JDBC 操作模板
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
					jdbcTemplate
							.setQueryTimeout((int) template.getQueryTimeout().getSeconds());
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
			public NamedParameterJdbcTemplate namedParameterJdbcTemplate(
					JdbcTemplate jdbcTemplate) {
				return new NamedParameterJdbcTemplate(jdbcTemplate);
			}
		}
	}
	
	/**
	 * 数据库事务
	 * @author lifeng
	 */
	@org.springframework.context.annotation.Configuration
	@ConditionalOnClass({ JdbcTemplate.class, PlatformTransactionManager.class })
	@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
	@ConditionalOnSingleCandidate(DataSource.class)
	public class DataSourceTransactionManagerAutoConfiguration {
		
		private final DataSource dataSource;

		private final TransactionManagerCustomizers transactionManagerCustomizers;

		DataSourceTransactionManagerAutoConfiguration(DataSource dataSource,
				ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
			this.dataSource = dataSource;
			this.transactionManagerCustomizers = transactionManagerCustomizers
					.getIfAvailable();
		}

		@Bean
		@ConditionalOnMissingBean(PlatformTransactionManager.class)
		public DataSourceTransactionManager transactionManager(
				DataSourceProperties properties) {
			DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(
					this.dataSource);
			if (this.transactionManagerCustomizers != null) {
				this.transactionManagerCustomizers.customize(transactionManager);
			}
			return transactionManager;
		}
	}
	
	/**
	 * Mybatis
	 * @author lifeng
	 *
	 */
	@org.springframework.context.annotation.Configuration
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
		

		public MybatisAutoConfiguration(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider,
				ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider,
				ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
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