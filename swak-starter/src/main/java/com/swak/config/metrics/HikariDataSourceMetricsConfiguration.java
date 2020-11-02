package com.swak.config.metrics;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import com.swak.config.jdbc.DataSourceAutoConfiguration;
import com.swak.meters.MetricsFactory;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.dropwizard.CodahaleMetricsTrackerFactory;

/**
 * 如果是 HikariDataSource 则启用
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(HikariDataSource.class)
@ConditionalOnBean({ MetricsFactory.class })
@AutoConfigureAfter({ MetricsAutoConfiguration.class, DataSourceAutoConfiguration.class })
public class HikariDataSourceMetricsConfiguration {

	@Autowired
	public void hikariDataSourceMetricsPostProcessor(MetricsFactory metricsFactory,
			Map<String, DataSource> dataSources) {
		dataSources.forEach((beanName, dataSource) -> {
			if (dataSource instanceof HikariDataSource) {
				HikariDataSource hdataSource = (HikariDataSource) dataSource;
				if (hdataSource.getMetricRegistry() == null && hdataSource.getMetricsTrackerFactory() == null) {
					hdataSource.setMetricsTrackerFactory(
							new CodahaleMetricsTrackerFactory(metricsFactory.metricRegistry()));
				}
			}
		});
	}
}