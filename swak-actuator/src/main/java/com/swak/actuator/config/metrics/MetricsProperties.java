package com.swak.actuator.config.metrics;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties("spring.metrics")
public class MetricsProperties {

	/**
	 * Whether auto-configured MeterRegistry implementations should be bound to the global
	 * static registry on Metrics. For testing, set this to 'false' to maximize test
	 * independence.
	 */
	private boolean useGlobalRegistry = true;

	/**
	 * Whether meter IDs starting-with the specified name should be enabled. The longest
	 * match wins, the key `all` can also be used to configure all meters.
	 */
	private Map<String, Boolean> enable = new LinkedHashMap<>();
	
	private final Distribution distribution = new Distribution();
	
	public Distribution getDistribution() {
		return this.distribution;
	}

	public boolean isUseGlobalRegistry() {
		return useGlobalRegistry;
	}

	public void setUseGlobalRegistry(boolean useGlobalRegistry) {
		this.useGlobalRegistry = useGlobalRegistry;
	}

	public Map<String, Boolean> getEnable() {
		return enable;
	}

	public void setEnable(Map<String, Boolean> enable) {
		this.enable = enable;
	}
	
	public static class Distribution {

		/**
		 * Whether meter IDs starting-with the specified name should be publish percentile
		 * histograms. Monitoring systems that support aggregable percentile calculation
		 * based on a histogram be set to true. For other systems, this has no effect. The
		 * longest match wins, the key `all` can also be used to configure all meters.
		 */
		private Map<String, Boolean> percentilesHistogram = new LinkedHashMap<>();

		/**
		 * Specific computed non-aggregable percentiles to ship to the backend for meter
		 * IDs starting-with the specified name. The longest match wins, the key `all` can
		 * also be used to configure all meters.
		 */
		private Map<String, double[]> percentiles = new LinkedHashMap<>();

		/**
		 * Specific SLA boundaries for meter IDs starting-with the specified name. The
		 * longest match wins, the key `all` can also be used to configure all meters.
		 * Counters will be published for each specified boundary. Values can be specified
		 * as a long or as a Duration value (for timer meters, defaulting to ms if no unit
		 * specified).
		 */
		private Map<String, ServiceLevelAgreementBoundary[]> sla = new LinkedHashMap<>();

		public Map<String, Boolean> getPercentilesHistogram() {
			return this.percentilesHistogram;
		}

		public void setPercentilesHistogram(Map<String, Boolean> percentilesHistogram) {
			Assert.notNull(percentilesHistogram, "PercentilesHistogram must not be null");
			this.percentilesHistogram = percentilesHistogram;
		}

		public Map<String, double[]> getPercentiles() {
			return this.percentiles;
		}

		public void setPercentiles(Map<String, double[]> percentiles) {
			Assert.notNull(percentiles, "Percentiles must not be null");
			this.percentiles = percentiles;
		}

		public Map<String, ServiceLevelAgreementBoundary[]> getSla() {
			return this.sla;
		}

		public void setSla(Map<String, ServiceLevelAgreementBoundary[]> sla) {
			Assert.notNull(sla, "SLA must not be null");
			this.sla = sla;
		}

	}
}
