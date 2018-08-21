package com.swak.actuator.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.swak.actuator.config.metrics.MeterRegistryPostProcessor;
import com.swak.actuator.config.metrics.MetricsProperties;
import com.swak.actuator.config.metrics.PropertiesMeterFilter;
import com.swak.meters.Monitor;

import ch.qos.logback.classic.LoggerContext;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;

/**
 * 这个配置想的作用是将
 * 
 * 配置 MeterRegistry 和 提供一些默认的指标MeterBinder
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(Timed.class)
@EnableConfigurationProperties(MetricsProperties.class)
public class MetricsAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Clock micrometerClock() {
		return Clock.SYSTEM;
	}
	
	@Bean
	@Order(0)
	public PropertiesMeterFilter propertiesMeterFilter(MetricsProperties properties) {
		return new PropertiesMeterFilter(properties);
	}
	
	@Bean
	public MeterRegistryPostProcessor meterRegistryPostProcessor(
			ApplicationContext context) {
		return new MeterRegistryPostProcessor(context);
	}
	
	/**
	 *  jvm 
	 * @author lifeng
	 *
	 */
	@Configuration
	@ConditionalOnProperty(value = "spring.metrics.binders.jvm.enabled", matchIfMissing = true)
	static class JvmMeterBindersConfiguration {
		
		@Bean
		@ConditionalOnMissingBean
		public JvmGcMetrics jvmGcMetrics() {
			return new JvmGcMetrics();
		}
		
		@Bean
		@ConditionalOnMissingBean
		public JvmMemoryMetrics jvmMemoryMetrics() {
			return new JvmMemoryMetrics();
		}
		
		@Bean
		@ConditionalOnMissingBean
		public JvmThreadMetrics jvmThreadMetrics() {
			return new JvmThreadMetrics();
		}
		
		@Bean
		@ConditionalOnMissingBean
		public ClassLoaderMetrics classLoaderMetrics() {
			return new ClassLoaderMetrics();
		}
	}
	
	/**
	 * 程序资源
	 * @author lifeng
	 */
	@Configuration
	static class MeterBindersConfiguration {
		
		@Bean
		@ConditionalOnClass({LoggerContext.class})
		@ConditionalOnMissingBean
		@ConditionalOnProperty(value = "spring.metrics.binders.logback.enabled", matchIfMissing = true)
		public LogbackMetrics logbackMetrics() {
			return new LogbackMetrics();
		}
		
		@Bean
		@ConditionalOnProperty(value = "spring.metrics.binders.uptime.enabled", matchIfMissing = true)
		@ConditionalOnMissingBean
		public UptimeMetrics uptimeMetrics() {
			return new UptimeMetrics();
		}

		@Bean
		@ConditionalOnProperty(value = "spring.metrics.binders.processor.enabled", matchIfMissing = true)
		@ConditionalOnMissingBean
		public ProcessorMetrics processorMetrics() {
			return new ProcessorMetrics();
		}

		@Bean
		@ConditionalOnProperty(name = "spring.metrics.binders.files.enabled", matchIfMissing = true)
		@ConditionalOnMissingBean
		public FileDescriptorMetrics fileDescriptorMetrics() {
			return new FileDescriptorMetrics();
		}
	}
	
	/**
	 * 程序状态
	 * @author lifeng
	 */
	@Configuration
	@ConditionalOnProperty(value = "spring.metrics.binders.system.enabled", matchIfMissing = true)
	static class SystemMeterBindersConfiguration {
		
		@Bean
		@ConditionalOnMissingBean
		public Monitor meterCenter() {
			return new Monitor();
		}
	}
}
