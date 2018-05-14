package com.swak.actuator.config.metrics.export;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.common.Constants;

import io.micrometer.core.instrument.simple.CountingMode;
import io.micrometer.core.instrument.simple.SimpleConfig;

/**
 * 最简单的 metrics 输出
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.ACTUATOR_METRICS + ".export.simple")
public class SimpleProperties implements SimpleConfig {

	/**
	 * Step size (i.e. reporting frequency) to use.
	 */
	private Duration step = Duration.ofMinutes(1);

	/**
	 * Counting mode.
	 */
	private CountingMode mode = CountingMode.CUMULATIVE;

	public Duration getStep() {
		return this.step;
	}

	public void setStep(Duration step) {
		this.step = step;
	}

	public CountingMode getMode() {
		return this.mode;
	}

	public void setMode(CountingMode mode) {
		this.mode = mode;
	}

	protected final <V> V get(Function<SimpleProperties, V> getter, Supplier<V> fallback) {
		V value = getter.apply(this);
		return (value != null ? value : fallback.get());
	}
	
	@Override
	public String get(String key) {
		return null;
	}
	
	@Override
	public Duration step() {
		return get(SimpleProperties::getStep, SimpleConfig.super::step);
	}

	@Override
	public CountingMode mode() {
		return get(SimpleProperties::getMode, SimpleConfig.super::mode);
	}
}
