package com.swak.config.metrics;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.config.customizer.SyncDataSourceOptionsCustomizer;
import com.swak.meters.MetricsFactory;
import com.swak.persistence.metrics.MetricsCollector;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.dropwizard.CodahaleMetricsTrackerFactory;

/**
 * 启用指标监控
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ DataSource.class })
@ConditionalOnBean({ MetricsFactory.class })
@AutoConfigureAfter({ MetricsAutoConfiguration.class })
public class SyncDataSourceMetricsConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = Constants.ACTUATOR_METRICS, name = "syncdb.open", matchIfMissing = true)
	public SyncDataSourceOptionsCustomizer dataSourceOptionsCustomizer(MetricsFactory metricsFactory) {
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

	@Bean
	@ConditionalOnProperty(prefix = Constants.ACTUATOR_METRICS, name = "syncSql.open", matchIfMissing = true)
	public SyncDataSourceOptionsCustomizer sqlOptionsCustomizer(MetricsFactory metricsFactory) {
		return (dataSource) -> {
			MetricsCollector.registryMetricsFactory(metricsFactory);
		};
	}
}