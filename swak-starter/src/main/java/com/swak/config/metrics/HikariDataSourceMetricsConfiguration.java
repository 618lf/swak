package com.swak.config.metrics;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.customizer.DataSourceOptionsCustomizer;
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
@AutoConfigureAfter({ MetricsAutoConfiguration.class })
public class HikariDataSourceMetricsConfiguration {

	@Bean
	public DataSourceOptionsCustomizer dataSourceOptionsCustomizer(MetricsFactory metricsFactory) {
		return (dataSource) -> {
			if (dataSource instanceof HikariDataSource) {
				HikariDataSource hdataSource = (HikariDataSource) dataSource;
				if (hdataSource.getMetricRegistry() == null && hdataSource.getMetricsTrackerFactory() == null) {
					hdataSource.setMetricsTrackerFactory(
							new CodahaleMetricsTrackerFactory(metricsFactory.metricRegistry()));
				}
			}
		};
	}
}