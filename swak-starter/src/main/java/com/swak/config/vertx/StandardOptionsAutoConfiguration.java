package com.swak.config.vertx;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.swak.reactivex.transport.TransportMode;
import com.swak.vertx.config.VertxProperties;

import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;

/**
 * 基础的 options
 * 
 * @author lifeng
 */
@AutoConfigureAfter(name = "com.swak.metrics.vertx.MetricsOptionsAutoConfiguration")
@ConditionalOnMissingBean(VertxOptions.class)
@EnableConfigurationProperties(VertxProperties.class)
public class StandardOptionsAutoConfiguration {

	/**
	 * 构建配置
	 * 
	 * @param properties
	 * @return
	 */
	@Bean
	public VertxOptions vertxOptions(VertxProperties properties) {
		VertxOptions vertxOptions = new VertxOptions();
		if (properties.getMode() == TransportMode.EPOLL) {
			vertxOptions.setPreferNativeTransport(true);
		}
		System.setProperty("vertx.disableWebsockets", Boolean.TRUE.toString());
		vertxOptions.setEventLoopPoolSize(properties.getEventLoopPoolSize());
		vertxOptions.setWorkerPoolSize(properties.getWorkerThreads());
		vertxOptions.setInternalBlockingPoolSize(properties.getInternalBlockingThreads());
		vertxOptions.setMaxEventLoopExecuteTime(properties.getMaxEventLoopExecuteTime());
		vertxOptions.setMaxWorkerExecuteTime(properties.getMaxWorkerExecuteTime());
		return vertxOptions;
	}

	/**
	 * 构建配置
	 * 
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
