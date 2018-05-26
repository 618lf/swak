package com.swak.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.swak.cache.redis.RedisLocalCache;
import com.swak.eventbus.EventBus;
import com.swak.eventbus.EventBusBoot;
import com.swak.eventbus.EventConsumer;
import com.swak.eventbus.EventProducer;
import com.swak.eventbus.impl.RedisEventProducer;
import com.swak.eventbus.system.SystemEventProducer;
import com.swak.eventbus.system.SystemEventPublisher;

/**
 * EventBus 自动配置
 * @author lifeng
 */
public class EventBusConfigurationSupport {

	/**
	 * 注册 event bus 和相应的消费者
	 * @param consumers
	 * @return
	 */
	@Bean
	public EventBus eventBus(List<EventConsumer> consumers) {
		EventBus eventBus = new EventBus();
		consumers.stream().forEach(consumer -> eventBus.addConsumer(consumer));
		return eventBus;
	}
	
	/**
	 * 注册
	 * @return
	 */
	@Bean
	public EventBusBoot eventBusBooter() {
		return new EventBusBoot();
	}
	
	/**
	 * 创建一个模板, 并设置 RedisLocalCache 的发布
	 * @return
	 */
	@Bean
	public EventProducer eventTemplate(RedisLocalCache localCache) {
		EventProducer producer = new RedisEventProducer();
		localCache.setProducer(producer);
		return producer;
	}
	
	/**
	 * 系统事件
	 * @param eventProducer
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(SystemEventPublisher.class)
	public SystemEventPublisher systemEventPublisher(EventProducer eventProducer) {
		return new SystemEventProducer(eventProducer);
	}
}