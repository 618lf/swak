package com.swak.actuator.config.metrics.jdbc;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.config.MetricsAutoConfiguration;
import com.swak.actuator.config.metrics.export.SimpleMetricsExportAutoConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * 如果是 HikariDataSource 则启用
 * @author lifeng
 */
@Configuration
@ConditionalOnBean({MeterRegistry.class})
@AutoConfigureAfter({ MetricsAutoConfiguration.class, DataSourceAutoConfiguration.class,
	SimpleMetricsExportAutoConfiguration.class })
public class HikariDataSourceMetricsConfiguration {
	
	MeterRegistry registry;
	
	public HikariDataSourceMetricsConfiguration(MeterRegistry registry) {
		this.registry = registry;
	}

	@Autowired
	public void hikariDataSourceMetricsPostProcessor(
			Map<String, DataSource> dataSources, MeterRegistry registry) {
		dataSources.forEach(this::bindDataSourceToRegistry);
	}
	
	private void bindDataSourceToRegistry(String beanName, DataSource dataSource) {
		if (dataSource instanceof HikariDataSource) {
			HikariDataSource hdataSource = (HikariDataSource)dataSource;
			if (hdataSource.getMetricRegistry() == null && hdataSource.getMetricsTrackerFactory() == null) {
				hdataSource.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory(registry));
			}
		}
	}
}