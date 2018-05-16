package com.swak.actuator.metrics.jdbc;

import java.util.function.Function;

import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.util.Assert;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;

public class DataSourcePoolMetrics implements MeterBinder {

	private final DataSourcePoolMetadata dataSourcePoolMetadata;
	private final Iterable<Tag> tags;

	public DataSourcePoolMetrics(DataSourcePoolMetadata dataSourcePoolMetadata,
			String metadataName, Iterable<Tag> tags) {
		Assert.notNull(dataSourcePoolMetadata, "Metadata must not be null");
		this.dataSourcePoolMetadata = dataSourcePoolMetadata;
		this.tags = Tags.concat(tags, "name", metadataName);
	}

	@Override
	public void bindTo(MeterRegistry registry) {
		bindPoolMetadata(registry, "active", DataSourcePoolMetadata::getActive);
		bindPoolMetadata(registry, "max", DataSourcePoolMetadata::getMax);
		bindPoolMetadata(registry, "min", DataSourcePoolMetadata::getMin);
	}

	private <N extends Number> void bindPoolMetadata(MeterRegistry registry, String metricName,
			Function<DataSourcePoolMetadata, N> function) {
		registry.gauge("jdbc.connections." + metricName, this.tags, this.dataSourcePoolMetadata,
				(m) -> function.apply(m).doubleValue());
	}
}