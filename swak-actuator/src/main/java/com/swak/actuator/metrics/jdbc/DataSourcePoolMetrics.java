package com.swak.actuator.metrics.jdbc;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;

public class DataSourcePoolMetrics implements MeterBinder {

	private final DataSource dataSource;
	
	private final CachingDataSourcePoolMetadataProvider metadataProvider;
	
	private final Iterable<Tag> tags;

	public DataSourcePoolMetrics(DataSource dataSource,
			Collection<DataSourcePoolMetadataProvider> metadataProviders,
			String dataSourceName, Iterable<Tag> tags) {
		this(dataSource, new CompositeDataSourcePoolMetadataProvider(metadataProviders),
				dataSourceName, tags);
	}

	public DataSourcePoolMetrics(DataSource dataSource,
			DataSourcePoolMetadataProvider metadataProvider, String name,
			Iterable<Tag> tags) {
		Assert.notNull(dataSource, "DataSource must not be null");
		Assert.notNull(metadataProvider, "MetadataProvider must not be null");
		this.dataSource = dataSource;
		this.metadataProvider = new CachingDataSourcePoolMetadataProvider(
				metadataProvider);
		this.tags = Tags.concat(tags, "name", name);
	}

	@Override
	public void bindTo(MeterRegistry registry) {
		if (this.metadataProvider.getDataSourcePoolMetadata(this.dataSource) != null) {
			bindPoolMetadata(registry, "active", DataSourcePoolMetadata::getActive);
			bindPoolMetadata(registry, "max", DataSourcePoolMetadata::getMax);
			bindPoolMetadata(registry, "min", DataSourcePoolMetadata::getMin);
		}
	}

	private <N extends Number> void bindPoolMetadata(MeterRegistry registry, String metricName,
			Function<DataSourcePoolMetadata, N> function) {
		bindDataSource(registry, metricName, this.metadataProvider.getValueFunction(function));
	}

	private <N extends Number> void bindDataSource(MeterRegistry registry, String metricName,
			Function<DataSource, N> function) {
		if (function.apply(this.dataSource) != null) {
			registry.gauge("jdbc.connections." + metricName, this.tags, this.dataSource,
					(m) -> function.apply(m).doubleValue());
		}
	}

	private static class CachingDataSourcePoolMetadataProvider implements DataSourcePoolMetadataProvider {

		private static final Map<DataSource, DataSourcePoolMetadata> cache = new ConcurrentReferenceHashMap<>();

		private final DataSourcePoolMetadataProvider metadataProvider;

		CachingDataSourcePoolMetadataProvider(DataSourcePoolMetadataProvider metadataProvider) {
			this.metadataProvider = metadataProvider;
		}

		public <N extends Number> Function<DataSource, N> getValueFunction(
				Function<DataSourcePoolMetadata, N> function) {
			return (dataSource) -> function.apply(getDataSourcePoolMetadata(dataSource));
		}

		@Override
		public DataSourcePoolMetadata getDataSourcePoolMetadata(DataSource dataSource) {
			DataSourcePoolMetadata metadata = cache.get(dataSource);
			if (metadata == null) {
				metadata = this.metadataProvider.getDataSourcePoolMetadata(dataSource);
				cache.put(dataSource, metadata);
			}
			return metadata;
		}

	}	
}
