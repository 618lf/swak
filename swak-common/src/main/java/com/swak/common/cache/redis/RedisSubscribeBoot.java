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
		RedisUtils.getRedis().subscribe(redisLocal, redisLocal.getChannels());
	}

	/**
	 * 描述
	 */
	@Override
	public String describe() {
		return "本地缓存订阅服务";
	}
}