package com.swak.config.metrics;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.async.persistence.datasource.DataSource;
import com.swak.async.persistence.metrics.MetricsCollector;
import com.swak.config.customizer.AsyncDataSourceOptionsCustomizer;
import com.swak.meters.MetricsFactory;

/**
 * 如果是 HikariDataSource 则启用
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ DataSource.class })
@ConditionalOnBean({ MetricsFactory.class })
@AutoConfigureAfter({ MetricsAutoConfiguration.class })
@ConditionalOnProperty(prefix = Constants.ACTUATOR_METRICS, name = "asyncdb.open", matchIfMissing = true)
public class AsyncDataSourceMetricsConfiguration {

	@Bean
	public AsyncDataSourceOptionsCustomizer asyncDataSourceOptionsCustomizer(MetricsFactory metricsFactory) {
		return (dataSource) -> {
			MetricsCollector.registryMetricsFactory(metricsFactory);
		};
	}
}