package com.swak.actuator.config.metrics;

import io.micrometer.core.instrument.MeterRegistry;

@FunctionalInterface
public interface MeterRegistryCustomizer<T extends MeterRegistry> {

	/**
	 * Customize the given {@code registry}.
	 * @param registry the registry to customize
	 */
	void customize(T registry);
}
