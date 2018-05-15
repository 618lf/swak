package com.swak.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.swak.common.eventbus.EventProducer;
import com.swak.common.eventbus.system.SystemEventProducer;
import com.swak.common.eventbus.system.SystemEventPublisher;

/**
 * 系统事件
 * @author lifeng
 */
public class SystemEventConfigurationSupport {

	/**
	 * 默认会启用这个
	 * @param eventProducer
	 * @return
	 */
	@Bean
	@ConditionalOnBean(EventProducer.class)
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
	public SystemEventPublisher noSystemEventPublisher(EventProducer eventProducer) {
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