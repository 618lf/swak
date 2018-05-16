package com.swak.actuator.metrics.jdbc;

import org.springframework.core.Ordered;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;

import io.micrometer.core.instrument.MeterRegistry;

public class HikariDataSourceMetricsPostProcessor implements Ordered {

	public HikariDataSourceMetricsPostProcessor(HikariDataSource dataSource, MeterRegistry registry) {
		if (dataSource.getMetricRegistry() == null && dataSource.getMetricsTrackerFactory() == null) {
			dataSource.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory(registry));
		}
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}