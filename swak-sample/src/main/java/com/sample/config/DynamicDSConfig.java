package com.sample.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.google.common.collect.Maps;
import com.swak.Constants;
import com.swak.config.jdbc.database.DataSourceProperties;
import com.swak.config.jdbc.database.HikariDataSourceAutoConfiguration;
import com.swak.persistence.datasource.DataSourceHolder;
import com.swak.persistence.datasource.DynamicDataSource;
import com.swak.persistence.datasource.DynamicSourceAspect;

/**
 * 适合主从模式的多数据源配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableDynamicDS", matchIfMissing = false)
public class DynamicDSConfig {

	// ************ 主定义主配置的资源配置 ****************
	@ConfigurationProperties(prefix = "spring.datasource")
	class PrimaryDataSourceProperties extends DataSourceProperties {

	}

	@ConfigurationProperties(prefix = "spring.datasource.slave")
	class OrderDataSourceProperties extends DataSourceProperties {

	}

	/**
	 * 需要定义一个主数据配置-- 确定数据的类型
	 */
	@Bean
	@Primary
	public PrimaryDataSourceProperties primaryDataSourceProperties() {
		return new PrimaryDataSourceProperties();
	}

	@Bean
	public OrderDataSourceProperties orderDataSourceProperties() {
		return new OrderDataSourceProperties();
	}

	@Bean
	public DataSource primaryDataSource(PrimaryDataSourceProperties properties) {
		return new HikariDataSourceAutoConfiguration().hikariDataSource(properties);
	}

	@Bean
	public DataSource orderDataSource(OrderDataSourceProperties properties) {
		return new HikariDataSourceAutoConfiguration().hikariDataSource(properties);
	}

	@Bean
	@Primary
	public DataSource dynamicDataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource,
			@Qualifier("orderDataSource") DataSource orderDataSource) {

		// 配置多数据源
		Map<Object, Object> targetDataSources = Maps.newConcurrentMap();
		targetDataSources.put("master", primaryDataSource);
		targetDataSources.put("slave", orderDataSource);

		// 配置动态数据源
		DataSource dynamicDataSource = new DynamicDataSource(primaryDataSource, targetDataSources);
		DataSourceHolder.setDataSource(dynamicDataSource);
		return dynamicDataSource;
	}

	/**
	 * 通过 Aop 来开启自动数据源的切换
	 * 
	 * @return
	 */
	@Bean
	public DynamicSourceAspect dynamicSourceAspect() {
		return new DynamicSourceAspect();
	}
}