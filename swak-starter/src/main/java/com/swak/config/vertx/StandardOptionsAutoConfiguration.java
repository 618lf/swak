package com.swak.config.vertx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.swak.reactivex.transport.TransportMode;
import com.swak.vertx.config.VertxProperties;

import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.logging.LoggerFactory;

/**
 * 基础的 options
 * 
 * @author lifeng
 */
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
		System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME,
				"io.vertx.core.logging.SLF4JLogDelegateFactory");
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
