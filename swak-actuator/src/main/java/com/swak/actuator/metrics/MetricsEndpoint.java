package com.swak.actuator.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.annotation.Operation;
import com.swak.actuator.endpoint.annotation.Selector;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.lang.Nullable;

/**
 * An {@link Endpoint} for exposing the metrics held by a {@link MeterRegistry}.
 *
 * @author Jon Schneider
 * @author Phillip Webb
 * @since 2.0.0
 */
@Endpoint(id = "metrics")
public class MetricsEndpoint {
	
	private final MeterRegistry registry;

	public MetricsEndpoint(MeterRegistry registry) {
		this.registry = registry;
	}
	
	/**
	 * 所有注册的名称
	 * @return
	 */
	@Operation
	public Set<String> listNames() {
		Set<String> names = new LinkedHashSet<>();
		collectNames(names, this.registry);
		return names;
	}
	private void collectNames(Set<String> names, MeterRegistry registry) {
		if (registry instanceof CompositeMeterRegistry) {
			((CompositeMeterRegistry) registry).getRegistries()
					.forEach((member) -> collectNames(names, member));
		}
		else {
			registry.getMeters().stream().map(this::getName).forEach(names::add);
		}
	}
	private String getName(Meter meter) {
		return meter.getId().getName();
	}
	

	/**
	 * requiredMetricName will add the path
	 * @param requiredMetricName
	 * @param tag
	 * @return
	 */
	@Operation
	public MetricResponse metric(@Selector String requiredMetricName,
			@Nullable List<String> tag) {
		Assert.isTrue(tag == null || tag.stream().allMatch((t) -> t.contains(":")),
				"Each tag parameter must be in the form key:value");
		List<Tag> tags = parseTags(tag);
		List<Meter> meters = new ArrayList<>();
		collectMeters(meters, this.registry, requiredMetricName, tags);
		if (meters.isEmpty()) {
			return null;
		}
		Map<Statistic, Double> samples = getSamples(meters);
		Map<String, Set<String>> availableTags = getAvailableTags(meters);
		tags.forEach((t) -> availableTags.remove(t.getKey()));
		return new MetricResponse(requiredMetricName, asList(samples, Sample::new),
				asList(availableTags, AvailableTag::new));
	}
	
	private List<Tag> parseTags(List<String> tags) {
		if (tags == null) {
			return Collections.emptyList();
		}
		return tags.stream().map(this::parseTag).collect(Collectors.toList());
	}
	
	private Tag parseTag(String tag) {
		String[] parts = tag.split(":", 2);
		return Tag.of(parts[0], parts[1]);
	}
	
	private void collectMeters(List<Meter> meters, MeterRegistry registry, String name,
			Iterable<Tag> tags) {
		if (registry instanceof CompositeMeterRegistry) {
			((CompositeMeterRegistry) registry).getRegistries()
					.forEach((member) -> collectMeters(meters, member, name, tags));
		}
		else {
			meters.addAll(registry.find(name).tags(tags).meters());
		}
	}
	
	private Map<Statistic, Double> getSamples(List<Meter> meters) {
		Map<Statistic, Double> samples = new LinkedHashMap<>();
		meters.forEach((meter) -> mergeMeasurements(samples, meter));
		return samples;
	}
	
	private void mergeMeasurements(Map<Statistic, Double> samples, Meter meter) {
		meter.measure().forEach((measurement) -> samples.merge(measurement.getStatistic(),
				measurement.getValue(), mergeFunction(measurement.getStatistic())));
	}
	
	private BiFunction<Double, Double, Double> mergeFunction(Statistic statistic) {
		return (Statistic.MAX.equals(statistic) ? Double::max : Double::sum);
	}
	
	private Map<String, Set<String>> getAvailableTags(List<Meter> meters) {
		Map<String, Set<String>> availableTags = new HashMap<>();
		meters.forEach((meter) -> mergeAvailableTags(availableTags, meter));
		return availableTags;
	}
	
	private void mergeAvailableTags(Map<String, Set<String>> availableTags, Meter meter) {
		meter.getId().getTags().forEach((tag) -> {
			Set<String> value = Collections.singleton(tag.getValue());
			availableTags.merge(tag.getKey(), value, this::merge);
		});
	}
	
	private <T> Set<T> merge(Set<T> set1, Set<T> set2) {
		Set<T> result = new HashSet<>(set1.size() + set2.size());
		result.addAll(set1);
		result.addAll(set2);
		return result;
	}
	
	private <K, V, T> List<T> asList(Map<K, V> map, BiFunction<K, V, T> mapper) {
		return map.entrySet().stream()
				.map((entry) -> mapper.apply(entry.getKey(), entry.getValue()))
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	/**
	 * Response payload for a metric name selector.
	 */
	public static final class MetricResponse {

		private final String name;

		private final List<Sample> measurements;

		private final List<AvailableTag> availableTags;

		MetricResponse(String name, List<Sample> measurements,
				List<AvailableTag> availableTags) {
			this.name = name;
			this.measurements = measurements;
			this.availableTags = availableTags;
		}

		public String getName() {
			return this.name;
		}

		public List<Sample> getMeasurements() {
			return this.measurements;
		}

		public List<AvailableTag> getAvailableTags() {
			return this.availableTags;
		}

	}
	
	/**
	 * A set of tags for further dimensional drilldown and their potential values.
	 */
	public static final class AvailableTag {

		private final String tag;

		private final Set<String> values;

		AvailableTag(String tag, Set<String> values) {
			this.tag = tag;
			this.values = values;
		}

		public String getTag() {
			return this.tag;
		}

		public Set<String> getValues() {
			return this.values;
		}

	}
	
	/**
	 * A measurement sample combining a {@link Statistic statistic} and a value.
	 */
	public static final class Sample {

		private final Statistic statistic;

		private final Double value;

		Sample(Statistic statistic, Double value) {
			this.statistic = statistic;
			this.value = value;
		}

		public Statistic getStatistic() {
			return this.statistic;
		}

		public Double getValue() {
			return this.value;
		}

		@Override
		public String toString() {
			return "MeasurementSample{" + "statistic=" + this.statistic + ", value="
					+ this.value + '}';
		}

	}
}
