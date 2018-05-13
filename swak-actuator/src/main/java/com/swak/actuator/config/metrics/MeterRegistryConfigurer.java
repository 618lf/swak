package com.swak.actuator.config.metrics;

import java.util.Collection;
import java.util.Collections;

import org.springframework.boot.util.LambdaSafe;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.config.MeterFilter;

public class MeterRegistryConfigurer {

	private final Collection<MeterRegistryCustomizer<?>> customizers;

	private final Collection<MeterFilter> filters;

	private final Collection<MeterBinder> binders;

	private final boolean addToGlobalRegistry;

	MeterRegistryConfigurer(Collection<MeterBinder> binders,
			Collection<MeterFilter> filters,
			Collection<MeterRegistryCustomizer<?>> customizers,
			boolean addToGlobalRegistry) {
		this.binders = (binders != null ? binders : Collections.emptyList());
		this.filters = (filters != null ? filters : Collections.emptyList());
		this.customizers = (customizers != null ? customizers : Collections.emptyList());
		this.addToGlobalRegistry = addToGlobalRegistry;
	}

	void configure(MeterRegistry registry) {
		// Customizers must be applied before binders, as they may add custom
		// tags or alter timer or summary configuration.
		customize(registry);
		addFilters(registry);
		addBinders(registry);
		if (this.addToGlobalRegistry && registry != Metrics.globalRegistry) {
			Metrics.addRegistry(registry);
		}
	}

	@SuppressWarnings("unchecked")
	private void customize(MeterRegistry registry) {
		LambdaSafe.callbacks(MeterRegistryCustomizer.class, this.customizers, registry)
				.withLogger(MeterRegistryConfigurer.class)
				.invoke((customizer) -> customizer.customize(registry));
	}

	private void addFilters(MeterRegistry registry) {
		this.filters.forEach(registry.config()::meterFilter);
	}

	private void addBinders(MeterRegistry registry) {
		this.binders.forEach((binder) -> binder.bindTo(registry));
	}
}
