package com.swak.metrics.reporter;

import static com.codahale.metrics.MetricAttribute.COUNT;
import static com.codahale.metrics.MetricAttribute.M15_RATE;
import static com.codahale.metrics.MetricAttribute.M1_RATE;
import static com.codahale.metrics.MetricAttribute.M5_RATE;
import static com.codahale.metrics.MetricAttribute.MAX;
import static com.codahale.metrics.MetricAttribute.MEAN;
import static com.codahale.metrics.MetricAttribute.MEAN_RATE;
import static com.codahale.metrics.MetricAttribute.MIN;
import static com.codahale.metrics.MetricAttribute.P50;
import static com.codahale.metrics.MetricAttribute.P75;
import static com.codahale.metrics.MetricAttribute.P95;
import static com.codahale.metrics.MetricAttribute.P98;
import static com.codahale.metrics.MetricAttribute.P99;
import static com.codahale.metrics.MetricAttribute.P999;
import static com.codahale.metrics.MetricAttribute.STDDEV;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Counting;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metered;
import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.swak.metrics.metas.MapMeta;

/**
 * 指标上报
 * 
 * @author lifeng
 * @date 2020年12月8日 下午3:30:42
 */
public class MetricsReporter extends ScheduledReporter {

	/**
	 * Returns a new {@link Builder} for {@link MetricsReporter}.
	 *
	 * @param registry the registry to report
	 * @return a {@link Builder} instance for a {@link MetricsReporter}
	 */
	public static Builder forRegistry(MetricRegistry registry) {
		return new Builder(registry);
	}

	/**
	 * A builder for {@link MetricsReporter} instances. Defaults to logging to
	 * {@code metrics}, not using a marker, converting rates to events/second,
	 * converting durations to milliseconds, and not filtering metrics.
	 */
	public static class Builder {
		private final MetricRegistry registry;
		private Logger logger;
		private Marker marker;
		private String prefix;
		private TimeUnit rateUnit;
		private TimeUnit durationUnit;
		private MetricFilter filter;
		private ScheduledExecutorService executor;
		private boolean shutdownExecutorOnStop;
		private Set<MetricAttribute> disabledMetricAttributes;

		private Builder(MetricRegistry registry) {
			this.registry = registry;
			this.logger = LoggerFactory.getLogger("metrics");
			this.marker = null;
			this.prefix = "";
			this.rateUnit = TimeUnit.SECONDS;
			this.durationUnit = TimeUnit.MILLISECONDS;
			this.filter = MetricFilter.ALL;
			this.executor = null;
			this.shutdownExecutorOnStop = true;
			this.disabledMetricAttributes = Collections.emptySet();
		}

		/**
		 * Specifies whether or not, the executor (used for reporting) will be stopped
		 * with same time with reporter. Default value is true. Setting this parameter
		 * to false, has the sense in combining with providing external managed executor
		 * via {@link #scheduleOn(ScheduledExecutorService)}.
		 *
		 * @param shutdownExecutorOnStop if true, then executor will be stopped in same
		 *                               time with this reporter
		 * @return {@code this}
		 */
		public Builder shutdownExecutorOnStop(boolean shutdownExecutorOnStop) {
			this.shutdownExecutorOnStop = shutdownExecutorOnStop;
			return this;
		}

		/**
		 * Specifies the executor to use while scheduling reporting of metrics. Default
		 * value is null. Null value leads to executor will be auto created on start.
		 *
		 * @param executor the executor to use while scheduling reporting of metrics.
		 * @return {@code this}
		 */
		public Builder scheduleOn(ScheduledExecutorService executor) {
			this.executor = executor;
			return this;
		}

		/**
		 * Log metrics to the given logger.
		 *
		 * @param logger an SLF4J {@link Logger}
		 * @return {@code this}
		 */
		public Builder outputTo(Logger logger) {
			this.logger = logger;
			return this;
		}

		/**
		 * Mark all logged metrics with the given marker.
		 *
		 * @param marker an SLF4J {@link Marker}
		 * @return {@code this}
		 */
		public Builder markWith(Marker marker) {
			this.marker = marker;
			return this;
		}

		/**
		 * Prefix all metric names with the given string.
		 *
		 * @param prefix the prefix for all metric names
		 * @return {@code this}
		 */
		public Builder prefixedWith(String prefix) {
			this.prefix = prefix;
			return this;
		}

		/**
		 * Convert rates to the given time unit.
		 *
		 * @param rateUnit a unit of time
		 * @return {@code this}
		 */
		public Builder convertRatesTo(TimeUnit rateUnit) {
			this.rateUnit = rateUnit;
			return this;
		}

		/**
		 * Convert durations to the given time unit.
		 *
		 * @param durationUnit a unit of time
		 * @return {@code this}
		 */
		public Builder convertDurationsTo(TimeUnit durationUnit) {
			this.durationUnit = durationUnit;
			return this;
		}

		/**
		 * Only report metrics which match the given filter.
		 *
		 * @param filter a {@link MetricFilter}
		 * @return {@code this}
		 */
		public Builder filter(MetricFilter filter) {
			this.filter = filter;
			return this;
		}

		/**
		 * Don't report the passed metric attributes for all metrics (e.g. "p999",
		 * "stddev" or "m15"). See {@link MetricAttribute}.
		 *
		 * @param disabledMetricAttributes a set of {@link MetricAttribute}
		 * @return {@code this}
		 */
		public Builder disabledMetricAttributes(Set<MetricAttribute> disabledMetricAttributes) {
			this.disabledMetricAttributes = disabledMetricAttributes;
			return this;
		}

		/**
		 * Builds a {@link MetricsReporter} with the given properties.
		 *
		 * @return a {@link MetricsReporter}
		 */
		public MetricsReporter build() {
			return new MetricsReporter(registry, logger, marker, prefix, rateUnit, durationUnit, filter, executor,
					shutdownExecutorOnStop, disabledMetricAttributes);
		}
	}

	private final Logger logger;
	private final Marker marker;
	private final String prefix;

	private MetricsReporter(MetricRegistry registry, Logger logger, Marker marker, String prefix, TimeUnit rateUnit,
			TimeUnit durationUnit, MetricFilter filter, ScheduledExecutorService executor,
			boolean shutdownExecutorOnStop, Set<MetricAttribute> disabledMetricAttributes) {
		super(registry, "logger-reporter", filter, rateUnit, durationUnit, executor, shutdownExecutorOnStop,
				disabledMetricAttributes);
		this.logger = logger;
		this.marker = marker;
		this.prefix = prefix;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
			SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
		for (Entry<String, Gauge> entry : gauges.entrySet()) {
			logGauge(entry.getKey(), entry.getValue());
		}

		for (Entry<String, Counter> entry : counters.entrySet()) {
			logCounter(entry.getKey(), entry.getValue());
		}

		for (Entry<String, Histogram> entry : histograms.entrySet()) {
			logHistogram(entry.getKey(), entry.getValue());
		}

		for (Entry<String, Meter> entry : meters.entrySet()) {
			logMeter(entry.getKey(), entry.getValue());
		}

		for (Entry<String, Timer> entry : timers.entrySet()) {
			logTimer(entry.getKey(), entry.getValue());
		}
	}

	private void logTimer(String name, Timer timer) {
		final Snapshot snapshot = timer.getSnapshot();
		MapMeta meta = MapMeta.of();
		append(meta, "type", "TIMER");
		append(meta, "name", prefix(name));
		appendCountIfEnabled(meta, timer);
		appendLongDurationIfEnabled(meta, MIN, snapshot::getMin);
		appendLongDurationIfEnabled(meta, MAX, snapshot::getMax);
		appendDoubleDurationIfEnabled(meta, MEAN, snapshot::getMean);
		appendDoubleDurationIfEnabled(meta, STDDEV, snapshot::getStdDev);
		appendDoubleDurationIfEnabled(meta, P50, snapshot::getMedian);
		appendDoubleDurationIfEnabled(meta, P75, snapshot::get75thPercentile);
		appendDoubleDurationIfEnabled(meta, P95, snapshot::get95thPercentile);
		appendDoubleDurationIfEnabled(meta, P98, snapshot::get98thPercentile);
		appendDoubleDurationIfEnabled(meta, P99, snapshot::get99thPercentile);
		appendDoubleDurationIfEnabled(meta, P999, snapshot::get999thPercentile);
		appendMetered(meta, timer);
		append(meta, "rate_unit", getRateUnit());
		append(meta, "duration_unit", getDurationUnit());
		logger.info(marker, meta.toString());
	}

	private void logMeter(String name, Meter meter) {
		MapMeta meta = MapMeta.of();
		append(meta, "type", "METER");
		append(meta, "name", prefix(name));
		appendCountIfEnabled(meta, meter);
		appendMetered(meta, meter);
		append(meta, "rate_unit", getRateUnit());
		logger.info(marker, meta.toString());
	}

	private void logHistogram(String name, Histogram histogram) {
		final Snapshot snapshot = histogram.getSnapshot();
		MapMeta meta = MapMeta.of();
		append(meta, "type", "HISTOGRAM");
		append(meta, "name", prefix(name));
		appendCountIfEnabled(meta, histogram);
		appendLongIfEnabled(meta, MIN, snapshot::getMin);
		appendLongIfEnabled(meta, MAX, snapshot::getMax);
		appendDoubleIfEnabled(meta, MEAN, snapshot::getMean);
		appendDoubleIfEnabled(meta, STDDEV, snapshot::getStdDev);
		appendDoubleIfEnabled(meta, P50, snapshot::getMedian);
		appendDoubleIfEnabled(meta, P75, snapshot::get75thPercentile);
		appendDoubleIfEnabled(meta, P95, snapshot::get95thPercentile);
		appendDoubleIfEnabled(meta, P98, snapshot::get98thPercentile);
		appendDoubleIfEnabled(meta, P99, snapshot::get99thPercentile);
		appendDoubleIfEnabled(meta, P999, snapshot::get999thPercentile);
		logger.info(marker, meta.toString());
	}

	private void logCounter(String name, Counter counter) {
		MapMeta meta = MapMeta.of();
		append(meta, "type", "COUNTER");
		append(meta, "name", prefix(name));
		append(meta, COUNT.getCode(), counter.getCount());
		logger.info(marker, meta.toString());
	}

	private void logGauge(String name, Gauge<?> gauge) {
		MapMeta meta = MapMeta.of();
		append(meta, "type", "GAUGE");
		append(meta, "name", prefix(name));
		append(meta, "value", gauge.getValue());
		logger.info(marker, meta.toString());
	}

	private void appendLongDurationIfEnabled(Map<String, Object> meta, MetricAttribute metricAttribute,
			Supplier<Long> durationSupplier) {
		if (!getDisabledMetricAttributes().contains(metricAttribute)) {
			append(meta, metricAttribute.getCode(), convertDuration(durationSupplier.get()));
		}
	}

	private void appendDoubleDurationIfEnabled(Map<String, Object> meta, MetricAttribute metricAttribute,
			Supplier<Double> durationSupplier) {
		if (!getDisabledMetricAttributes().contains(metricAttribute)) {
			append(meta, metricAttribute.getCode(), convertDuration(durationSupplier.get()));
		}
	}

	private void appendLongIfEnabled(Map<String, Object> meta, MetricAttribute metricAttribute,
			Supplier<Long> valueSupplier) {
		if (!getDisabledMetricAttributes().contains(metricAttribute)) {
			append(meta, metricAttribute.getCode(), valueSupplier.get());
		}
	}

	private void appendDoubleIfEnabled(Map<String, Object> meta, MetricAttribute metricAttribute,
			Supplier<Double> valueSupplier) {
		if (!getDisabledMetricAttributes().contains(metricAttribute)) {
			append(meta, metricAttribute.getCode(), valueSupplier.get());
		}
	}

	private void appendCountIfEnabled(Map<String, Object> meta, Counting counting) {
		if (!getDisabledMetricAttributes().contains(COUNT)) {
			// append(b, COUNT.getCode(), counting.getCount());
			meta.put(COUNT.getCode(), counting.getCount());
		}
	}

	private void appendMetered(Map<String, Object> meta, Metered meter) {
		appendRateIfEnabled(meta, M1_RATE, meter::getOneMinuteRate);
		appendRateIfEnabled(meta, M5_RATE, meter::getFiveMinuteRate);
		appendRateIfEnabled(meta, M15_RATE, meter::getFifteenMinuteRate);
		appendRateIfEnabled(meta, MEAN_RATE, meter::getMeanRate);
	}

	private void appendRateIfEnabled(Map<String, Object> meta, MetricAttribute metricAttribute,
			Supplier<Double> rateSupplier) {
		if (!getDisabledMetricAttributes().contains(metricAttribute)) {
			append(meta, metricAttribute.getCode(), convertRate(rateSupplier.get()));
		}
	}

	private void append(Map<String, Object> meta, String key, long value) {
		meta.put(key, value);
	}

	private void append(Map<String, Object> meta, String key, double value) {
		meta.put(key, value);
	}

	private void append(Map<String, Object> meta, String key, String value) {
		meta.put(key, value);
	}

	private void append(Map<String, Object> meta, String key, Object value) {
		meta.put(key, value);
	}

	@Override
	protected String getRateUnit() {
		return "events/" + super.getRateUnit();
	}

	private String prefix(String... components) {
		return MetricRegistry.name(prefix, components);
	}
}
