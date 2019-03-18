package com.swak.config.vertx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.swak.reactivex.transport.TransportMode;
import com.swak.vertx.config.VertxProperties;

import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxJmxMetricsOptions;

/**
 * 代有监控 options
 * @author lifeng
 */
@ConditionalOnMissingBean(VertxOptions.class)
@ConditionalOnClass(MicrometerMetricsOptions.class)
@EnableConfigurationProperties(VertxProperties.class)
public class MetricsOptionsAutoConfiguration {

	/**
	 * 构建配置
	 * @param properties
	 * @return
	 */
	@Bean
	public VertxOptions vertxOptions(VertxProperties properties) {
		VertxOptions vertxOptions = new VertxOptions();

		// Metrics
		if (properties.isMetricAble()) {
			VertxJmxMetricsOptions jmxOptions = new VertxJmxMetricsOptions();
			jmxOptions.setEnabled(true).setDomain("vertx-metrics");
			vertxOptions.setMetricsOptions(
					new MicrometerMetricsOptions().setEnabled(true).setJmxMetricsOptions(jmxOptions));
		}

		// pool config
		if (properties.getMode() == TransportMode.EPOLL) {
			vertxOptions.setPreferNativeTransport(true);
		}
		vertxOptions.setEventLoopPoolSize(properties.getEventLoopPoolSize());
		vertxOptions.setWorkerPoolSize(properties.getWorkerThreads());
		vertxOptions.setInternalBlockingPoolSize(properties.getInternalBlockingThreads());
		return vertxOptions;
	}
	
	/**
	 * 构建配置
	 * @param properties
	 * @return
	 */
	@Bean
	public DeliveryOptions deliveryOptions(VertxProperties properties) {
		DeliveryOptions deliveryOptions = new DeliveryOptions();
		deliveryOptions.setSendTimeout(properties.getSendTimeout());
		return deliveryOptions;
	}
}
