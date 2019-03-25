package com.swak.actuator.config.metrics.jdbc;

import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.swak.actuator.config.metrics.export.MetricsExportAutoConfiguration;
import com.swak.actuator.metrics.jdbc.DataSourcePoolMetrics;
import com.swak.config.jdbc.DataSourceAutoConfiguration;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
@AutoConfigureAfter({ MetricsExportAutoConfiguration.class, DataSourceAutoConfiguration.class})
@ConditionalOnBean({ DataSource.class, MeterRegistry.class })
public class DataSourcePoolMetricsAutoConfiguration {

	private static final String DATASOURCE_SUFFIX = "DataSourcePoolMetadata";
	
	private final MeterRegistry registry;

	public DataSourcePoolMetricsAutoConfiguration(MeterRegistry registry) {
		this.registry = registry;
	}
	
	@Autowired
	public void bindDataSourcesToRegistry(Map<String, DataSourcePoolMetadata> dataSourcePoolMetadatas) {
		dataSourcePoolMetadatas.forEach(this::bindDataSourceToRegistry);
	}
	
	private void bindDataSourceToRegistry(String beanName, DataSourcePoolMetadata dataSource) {
		String dataSourceName = getDataSourceName(beanName);
		new DataSourcePoolMetrics(dataSource, dataSourceName, Collections.emptyList()).bindTo(this.registry);
	}
	
	/**
	 * Get the name of a DataSource based on its {@code beanName}.
	 * @param beanName the name of the data source bean
	 * @return a name for the given data source
	 */
	private String getDataSourceName(String beanName) {
		if (beanName.length() > DATASOURCE_SUFFIX.length()
				&& StringUtils.endsWithIgnoreCase(beanName, DATASOURCE_SUFFIX)) {
			return beanName.substring(0, beanName.length() - DATASOURCE_SUFFIX.length());
		}
		return beanName;
	}
}
