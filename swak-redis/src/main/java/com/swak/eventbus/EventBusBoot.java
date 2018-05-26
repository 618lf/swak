package com.swak.eventbus;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.boot.AbstractBoot;
import com.swak.cache.redis.RedisUtils;

/**
 * 启动 Event bus
 * @author lifeng
 */
public class EventBusBoot extends AbstractBoot {

	@Autowired
	private RedisEventBus eventBus;
	
	@Override
	public String describe() {
		return "事件总线";
	}

	@Override
	public void init() {
		RedisUtils.listener(eventBus);
		eventBus.subscribe();
	}
}