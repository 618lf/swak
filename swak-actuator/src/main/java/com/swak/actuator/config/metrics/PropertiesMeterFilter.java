package com.swak.actuator.config.metrics;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.springframework.util.Assert;

import com.swak.actuator.config.metrics.MetricsProperties.Distribution;
import com.swak.utils.StringUtils;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;

/**
 * {@link MeterFilter} to apply settings from {@link MetricsProperties}.
 *
 * @author Jon Schneider
 * @author Phillip Webb
 * @since 2.0.0
 */
public class PropertiesMeterFilter implements MeterFilter {
	
	private static final ServiceLevelAgreementBoundary[] EMPTY_SLA = {};

	private MetricsProperties properties;
	

	public PropertiesMeterFilter(MetricsProperties properties) {
		Assert.notNull(properties, "Properties must not be null");
		this.properties = properties;
	}

	@Override
	public MeterFilterReply accept(Meter.Id id) {
		boolean enabled = lookup(this.properties.getEnable(), id, true);
		return (enabled ? MeterFilterReply.NEUTRAL : MeterFilterReply.DENY);
	}

	@Override
	public DistributionStatisticConfig configure(Meter.Id id,
			DistributionStatisticConfig config) {
		DistributionStatisticConfig.Builder builder = DistributionStatisticConfig
				.builder();
		Distribution distribution = this.properties.getDistribution();
		builder.percentilesHistogram(
				lookup(distribution.getPercentilesHistogram(), id, null));
		builder.percentiles(lookup(distribution.getPercentiles(), id, null));
		builder.sla(convertSla(id.getType(), lookup(distribution.getSla(), id, null)));
		return builder.build().merge(config);
	}

	private long[] convertSla(Meter.Type meterType, ServiceLevelAgreementBoundary[] sla) {
		long[] converted = Arrays.stream(sla != null ? sla : EMPTY_SLA)
				.map((candidate) -> candidate.getValue(meterType))
				.filter(Objects::nonNull).mapToLong(Long::longValue).toArray();
		return (converted.length != 0 ? converted : null);
	}

	private <T> T lookup(Map<String, T> values, Id id, T defaultValue) {
		String name = id.getName();
		while (StringUtils.hasLength(name)) {
			T result = values.get(name);
			if (result != null) {
				return result;
			}
			int lastDot = name.lastIndexOf('.');
			name = (lastDot != -1 ? name.substring(0, lastDot) : "");
		}
		return values.getOrDefault("all", defaultValue);
	}
}