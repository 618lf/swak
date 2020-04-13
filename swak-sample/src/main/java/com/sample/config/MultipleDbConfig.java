package com.sample.config;

import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.swak.Constants;
import com.swak.config.jdbc.MybatisAutoConfiguration;
import com.swak.config.jdbc.MybatisProperties;
import com.swak.config.jdbc.database.ConfigurationCustomizer;
import com.swak.config.jdbc.database.DataSourceProperties;
import com.swak.config.jdbc.database.HikariDataSourceAutoConfiguration;
import com.swak.persistence.dialect.Dialect;
import com.swak.persistence.mapper.MapperScan;
import com.swak.utils.Sets;

/**
 * 非主从数据源的配置实例： 从 Dao 层将数据源隔离
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableMultipleDb", matchIfMissing = false)
public class MultipleDbConfig {

	/**
	 * 主数据源的配置
	 * 
	 * @author lifeng
	 */
	@Configuration
	@MapperScan({ "com.tmt.system.dao", "com.tmt.gen.dao" })
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableMultipleDb", matchIfMissing = false)
	public static class PrimaryMapperConfig {

		// ************ 主定义主配置的资源配置 ****************

		@ConfigurationProperties(prefix = "spring.datasource")
		class PrimaryDataSourceProperties extends DataSourceProperties {

		}

		@ConfigurationProperties(prefix = "spring.mybatis")
		class PrimaryMybatisProperties extends MybatisProperties {

			@Override
			public Set<String> parseAutoMapperScanPackages() {
				return Sets.newHashSet("com.tmt.system.dao", "com.tmt.gen.dao");
			}
		}

		// ************ 1. 主数据源配置 这部分配置之后默认的数据源将失效，但事务配置没有失效 （关键点@Primary）
		// ****************

		@Bean
		public PrimaryDataSourceProperties primaryDataSourceProperties() {
			return new PrimaryDataSourceProperties();
		}

		@Bean
		@Primary
		public DataSource primaryDataSource(PrimaryDataSourceProperties properties) {
			return new HikariDataSourceAutoConfiguration().hikariDataSource(properties);
		}

		// ************ 2. 数据源的事务管理器 ， 配置这个之后 不会配置默认的事务管理器， 但是会配置一个默认的编程式事务
		// 模板， 不建议使用****************

		@Bean
		@Primary
		public DataSourceTransactionManager primaryTransactionManager(DataSource primaryDataSource,
				ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
			DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(primaryDataSource);
			transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(transactionManager));
			return transactionManager;
		}

		// @Transactional 使用此事务管理器 -- 也可以通过 Aop 来开启 包事务的管理模式
		// 如果多个数据源需要能统一事务则需要使用 JtaTransactionManager + AtomikosDataSource

		// ************ 3. 主 Mybatis 配置 这部分配置后默认的 Mybatis将失效，但是需要配置一个 @Primary
		// SqlSessionTemplate 因为默认的Dao 中是通过注解获取的 ****************

		@Bean
		public PrimaryMybatisProperties primaryMybatisProperties() {
			return new PrimaryMybatisProperties();
		}

		@Bean
		public MybatisAutoConfiguration primaryMybatisConfiguration(PrimaryMybatisProperties properties,
				ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader,
				ObjectProvider<DatabaseIdProvider> databaseIdProvider,
				ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
			return new MybatisAutoConfiguration(properties, interceptorsProvider, resourceLoader, databaseIdProvider,
					configurationCustomizersProvider);
		}

		@Bean
		@Primary
		public Dialect primaryDbDialect(MybatisAutoConfiguration primaryMybatisConfiguration,
				PrimaryDataSourceProperties dbProperties) {
			return primaryMybatisConfiguration.dbDialect(dbProperties);
		}

		@Bean
		public SqlSessionFactory primarySqlSessionFactory(DataSource primaryDataSource, Dialect primaryDbDialect,
				MybatisAutoConfiguration primaryMybatisConfiguration) throws Exception {
			return primaryMybatisConfiguration.sqlSessionFactory(primaryDataSource, primaryDbDialect);
		}

		@Bean
		@Primary
		public SqlSessionTemplate primarySessionTemplate(SqlSessionFactory primarySqlSessionFactory,
				MybatisAutoConfiguration primaryMybatisConfiguration) {
			return primaryMybatisConfiguration.sqlSessionTemplate(primarySqlSessionFactory);
		}
	}

	/**
	 * 其他数据源的配置
	 * 
	 * @author lifeng
	 */
	@Configuration
	@MapperScan(value = "com.sample.dao", sqlSessionTemplateRef = "orderSessionTemplate")
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableMultipleDb", matchIfMissing = false)
	public static class OrderMapperConfig {

		// ************ 订单系统 -- 配置的资源配置 ****************

		@ConfigurationProperties(prefix = "spring.datasource.order")
		class OrderDataSourceProperties extends DataSourceProperties {
		}

		@ConfigurationProperties(prefix = "spring.mybatis")
		class OrderMybatisProperties extends MybatisProperties {

			@Override
			public Set<String> parseAutoMapperScanPackages() {
				return Sets.newHashSet("com.sample.dao");
			}
		}

		// ************1. 订单系统 -- 数据源配置 ****************

		@Bean
		public OrderDataSourceProperties orderDataSourceProperties() {
			return new OrderDataSourceProperties();
		}

		@Bean
		public DataSource orderDataSource(OrderDataSourceProperties properties) {
			return new HikariDataSourceAutoConfiguration().hikariDataSource(properties);
		}

		// ************ 2. 数据源的事务管理器 -- 并通过AOP植入事务管理，不需要使用声明式事务 ****************

		@Bean
		public DataSourceTransactionManager orderTransactionManager(
				@Qualifier("orderDataSource") DataSource orderDataSource,
				ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
			DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(orderDataSource);
			transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(transactionManager));
			return transactionManager;
		}

		@Bean
		public TransactionInterceptor orderTxAdvice(
				@Qualifier("orderTransactionManager") DataSourceTransactionManager orderTransactionManager) {

			// 开启事务
			DefaultTransactionAttribute txAttr_REQUIRED = new DefaultTransactionAttribute();
			txAttr_REQUIRED.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

			// 只读
			DefaultTransactionAttribute txAttr_REQUIRED_READONLY = new DefaultTransactionAttribute();
			txAttr_REQUIRED_READONLY.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			txAttr_REQUIRED_READONLY.setReadOnly(true);

			// 事务配置
			NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
			source.addTransactionalMethod("save*", txAttr_REQUIRED);
			source.addTransactionalMethod("delete*", txAttr_REQUIRED);
			source.addTransactionalMethod("update*", txAttr_REQUIRED);
			source.addTransactionalMethod("exec*", txAttr_REQUIRED);
			source.addTransactionalMethod("set*", txAttr_REQUIRED);
			source.addTransactionalMethod("get*", txAttr_REQUIRED_READONLY);
			source.addTransactionalMethod("query*", txAttr_REQUIRED_READONLY);
			source.addTransactionalMethod("find*", txAttr_REQUIRED_READONLY);
			source.addTransactionalMethod("list*", txAttr_REQUIRED_READONLY);
			source.addTransactionalMethod("count*", txAttr_REQUIRED_READONLY);
			source.addTransactionalMethod("is*", txAttr_REQUIRED_READONLY);

			// 关联事务管理器
			return new TransactionInterceptor((TransactionManager)orderTransactionManager, source);
		}

		@Bean
		public Advisor orderTxAdviceAdvisor(@Qualifier("orderTxAdvice") TransactionInterceptor orderTxAdvice) {
			AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
			pointcut.setExpression("execution (public * com.sample.service..*.*(..)))");

			// 设置通过的顺序，排在声明式事务的前面（如果有）
			DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, orderTxAdvice);
			advisor.setOrder(-1000);
			return advisor;
		}

		// ************3. 订单系统 -- Mybatis 配置 ****************

		@Bean
		public OrderMybatisProperties orderMybatisProperties() {
			return new OrderMybatisProperties();
		}

		@Bean
		public MybatisAutoConfiguration orderMybatisConfiguration(OrderMybatisProperties properties,
				ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader,
				ObjectProvider<DatabaseIdProvider> databaseIdProvider,
				ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
			return new MybatisAutoConfiguration(properties, interceptorsProvider, resourceLoader, databaseIdProvider,
					configurationCustomizersProvider);
		}

		@Bean
		public Dialect orderDbDialect(MybatisAutoConfiguration orderMybatisConfiguration,
				OrderDataSourceProperties dbProperties) {
			return orderMybatisConfiguration.dbDialect(dbProperties);
		}

		@Bean
		public SqlSessionFactory orderSqlSessionFactory(@Qualifier("orderDataSource") DataSource orderDataSource,
				@Qualifier("orderDbDialect") Dialect orderDbDialect, MybatisAutoConfiguration orderMybatisConfiguration)
				throws Exception {
			return orderMybatisConfiguration.sqlSessionFactory(orderDataSource, orderDbDialect);
		}

		@Bean
		public SqlSessionTemplate orderSessionTemplate(
				@Qualifier("orderSqlSessionFactory") SqlSessionFactory orderSqlSessionFactory,
				MybatisAutoConfiguration orderMybatisConfiguration) {
			return orderMybatisConfiguration.sqlSessionTemplate(orderSqlSessionFactory);
		}
	}
}
