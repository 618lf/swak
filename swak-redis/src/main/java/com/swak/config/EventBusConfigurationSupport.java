package com.swak.config;

import java.util.List;

import org.springframework.context.annotation.Bean;

import com.swak.cache.redis.RedisLocalCache;
import com.swak.eventbus.EventBus;
import com.swak.eventbus.EventBusBoot;
import com.swak.eventbus.EventConsumer;
import com.swak.eventbus.EventProducer;
import com.swak.eventbus.RedisEventBus;
import com.swak.eventbus.impl.RedisEventProducer;

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
		RedisEventBus eventBus = new RedisEventBus();
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
}