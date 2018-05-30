package com.swak.config.flux;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.eventbus.EventProducer;
import com.swak.eventbus.system.SystemEventProducer;
import com.swak.eventbus.system.SystemEventPublisher;

/**
 * 系统事件
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 20)
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class SystemEventAutoConfiguration {

	/**
	 * 系统事件
	 * @param eventProducer
	 * @return
	 */
	@Bean
	@ConditionalOnBean(EventProducer.class)
	@ConditionalOnMissingBean(SystemEventPublisher.class)
	public SystemEventPublisher systemEventPublisher(EventProducer eventProducer) {
		return new SystemEventProducer(eventProducer);
	}
	
	/**
	 * 如果没有这个启动一个空的实现
	 * @param eventProducer
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(SystemEventPublisher.class)
	public SystemEventPublisher noSystemEventPublisher() {
		return new SystemEventPublisher() {
			@Override
			public void publishError(Throwable t) {}

			@Override
			public void publishSignIn(Object subject) {}

			@Override
			public void publishSignUp(Object subject) {}

			@Override
			public void publishLogout(Object subject) {}
		};
	}
}
