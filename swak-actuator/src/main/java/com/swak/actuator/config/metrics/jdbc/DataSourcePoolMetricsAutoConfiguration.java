package com.swak.actuator.config.metrics.jdbc;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.swak.actuator.config.MetricsAutoConfiguration;
import com.swak.actuator.config.metrics.export.SimpleMetricsExportAutoConfiguration;
import com.swak.actuator.metrics.jdbc.DataSourcePoolMetrics;
import com.swak.config.DataBaseConfigurationSupport.DataSourceAutoConfiguration;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
@AutoConfigureAfter({ MetricsAutoConfiguration.class, DataSourceAutoConfiguration.class,
		SimpleMetricsExportAutoConfiguration.class })
@ConditionalOnBean({ DataSource.class, DataSourcePoolMetadataProvider.class,
		MeterRegistry.class })
public class DataSourcePoolMetricsAutoConfiguration {

	private static final String DATASOURCE_SUFFIX = "dataSource";
	
	private final MeterRegistry registry;

	private final Collection<DataSourcePoolMetadataProvider> metadataProviders;

	public DataSourcePoolMetricsAutoConfiguration(MeterRegistry registry,
			Collection<DataSourcePoolMetadataProvider> metadataProviders) {
		this.registry = registry;
		this.metadataProviders = metadataProviders;
	}
	
	@Autowired
	public void bindDataSourcesToRegistry(Map<String, DataSource> dataSources) {
		dataSources.forEach(this::bindDataSourceToRegistry);
	}
	
	private void bindDataSourceToRegistry(String beanName, DataSource dataSource) {
		String dataSourceName = getDataSourceName(beanName);
		new DataSourcePoolMetrics(dataSource, this.metadataProviders, dataSourceName,
				Collections.emptyList()).bindTo(this.registry);
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
