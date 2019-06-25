package com.swak.metrics.jdbc;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricRegistry;
import com.swak.config.jdbc.DataSourceAutoConfiguration;
import com.swak.metrics.MetricsAutoConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.dropwizard.CodahaleMetricsTrackerFactory;

/**
 * 如果是 HikariDataSource 则启用
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(HikariDataSource.class)
@ConditionalOnBean({ MetricRegistry.class })
@AutoConfigureAfter({ MetricsAutoConfiguration.class, DataSourceAutoConfiguration.class })
public class HikariDataSourceMetricsConfiguration {

	private MetricRegistry registry;

	public HikariDataSourceMetricsConfiguration(MetricRegistry registry) {
		this.registry = registry;
	}

	@Autowired
	public void hikariDataSourceMetricsPostProcessor(Map<String, DataSource> dataSources) {
		dataSources.forEach(this::bindDataSourceToRegistry);
	}

	private void bindDataSourceToRegistry(String beanName, DataSource dataSource) {
		if (dataSource instanceof HikariDataSource) {
			HikariDataSource hdataSource = (HikariDataSource) dataSource;
			if (hdataSource.getMetricRegistry() == null && hdataSource.getMetricsTrackerFactory() == null) {
				hdataSource.setMetricsTrackerFactory(new CodahaleMetricsTrackerFactory(registry));
			}
		}
	}
}