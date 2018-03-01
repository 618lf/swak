package com.swak.common.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.common.boot.AbstractBoot;

/**
 * 订阅
 * @author root
 */
public class RedisSubscribeBoot extends AbstractBoot {

	@Autowired
	private RedisLocalCache redisLocal;
	
	/**
	 * 初始化加载
	 */
	@Override
	public void init() {
		// 订阅消息
		new Thread(new Runnable() {
			@Override
			public void run() {
				RedisUtils.getRedis().subscribe(redisLocal, redisLocal.getChannels());
			}
		}).start();
	}

	/**
	 * 描述
	 */
	@Override
	public String describe() {
		return "本地缓存订阅服务";
	}
}