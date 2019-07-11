package com.swak.metrics;

import static com.swak.Application.APP_LOGGER;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Reporter;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.swak.meters.MetricsFactory;
import com.swak.metrics.annotation.MetricBinder;
import com.swak.metrics.impl.CodahaleMetricsFactory;

/**
 * 这个配置想的作用是将
 * 
 * 配置 MeterRegistry 和 提供一些默认的指标
 * 
 * @author lifeng
 */
@Configuration
public class MetricsAutoConfiguration {
	
	public MetricsAutoConfiguration() {
		APP_LOGGER.debug("Metrics Monitor");
	}

	/**
	 * MetricsFactory
	 * 
	 * @return
	 */
	@Bean
	public CodahaleMetricsFactory metricsFactory() {
		return new CodahaleMetricsFactory(new MetricRegistry());
	}

	/**
	 * 用于处理在启动过程中的指标注册
	 * 
	 * @param context
	 * @return
	 */
	@Bean
	public MetricRegistryPostProcessor meterRegistryPostProcessor(MetricsFactory metricsFactory) {
		return new MetricRegistryPostProcessor(metricsFactory);
	}

	/**
	 * reporter
	 * 
	 * @author lifeng
	 */
	@Configuration
	static class MetricsReporterConfiguration {

		/**
		 * 默认使用 日志记录
		 * 
		 * @param metricRegistry
		 * @return
		 */
		@Bean
		@ConditionalOnMissingBean(Reporter.class)
		public Slf4jReporter slf4jReporter(MetricsFactory metricsFactory) {
			return Slf4jReporter.forRegistry(metricsFactory.metricRegistry())
					.outputTo(LoggerFactory.getLogger("com.swak.metrics")).convertRatesTo(TimeUnit.SECONDS)
					.convertDurationsTo(TimeUnit.MILLISECONDS).build();
		}
	}

	/**
	 * jvm
	 * 
	 * @author lifeng
	 */
	@Configuration
	static class JvmMeterBindersConfiguration {

		@Bean
		public MetricBinder jvmGcMetrics() {
			return (registry) -> {
				registry.registerAll("jvm.gc", new GarbageCollectorMetricSet());
			};
		}

		@Bean
		public MetricBinder jvmMemoryMetrics() {
			return (registry) -> {
				registry.registerAll("jvm.memory", new MemoryUsageGaugeSet());
			};
		}

		@Bean
		public MetricBinder jvmThreadMetrics() {
			return (registry) -> {
				registry.registerAll("jvm.thread", new ThreadStatesGaugeSet());
			};
		}

		@Bean
		public MetricBinder classLoaderMetrics() {
			return (registry) -> {
				registry.registerAll("jvm.classloader", new ClassLoadingGaugeSet());
			};
		}

		@Bean
		public MetricBinder fileDescriptorMetrics() {
			return (registry) -> {
				registry.register("jvm.fd", new FileDescriptorRatioGauge());
			};
		}

		@Bean
		public MetricBinder bufferPoolMetricSet() {
			return (registry) -> {
				registry.register("jvm.buffer", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
			};
		}
	}
}