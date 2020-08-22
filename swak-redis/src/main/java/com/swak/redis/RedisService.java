package com.swak.redis;

import java.util.concurrent.TimeUnit;

import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.TimerContext;
import com.swak.reactivex.threads.TimerContext.ScheduledTimerTask;

/**
 * Redis 的操作模板
 * 
 * @author lifeng
 * @date 2020年8月19日 下午4:28:59
 */
public class RedisService {
	private static TimerContext TIMEOUT_TIMER = Contexts.createTimerContext("SWAK.REDIS_TIMER", true, 1,
			TimeUnit.SECONDS, 100, TimeUnit.MILLISECONDS);
	private final RedisConnectionFactory<byte[], byte[]> factory;

	public RedisService(RedisConnectionFactory<byte[], byte[]> factory) {
		this.factory = factory;
	}

	/**
	 * 同步操作的命令集合
	 * 
	 * @return
	 */
	public RedisCommands<byte[], byte[]> sync() {
		return factory.getConnection(ConnectType.Standard).redisCommands();
	}

	/**
	 * 异步操作的命令集合
	 * 
	 * @return
	 */
	public RedisAsyncCommands<byte[], byte[]> async() {
		return factory.getConnection(ConnectType.Standard).redisAsyncCommands();
	}

	/**
	 * 基于Redis 的事件
	 * 
	 * @return
	 */
	public RedisAsyncPubSubCommands<byte[], byte[]> event() {
		return factory.getConnection(ConnectType.PubSub).redisAsyncPubSubCommands();
	}

	/**
	 * 创建延迟任务
	 * 
	 * @param task
	 * @param delay
	 * @param unit
	 * @return
	 */
	public ScheduledTimerTask newTimeoutTask(Runnable task, long delay, TimeUnit unit) {
		return TIMEOUT_TIMER.schedule(task, delay, unit);
	}

	/**
	 * 获得一把锁
	 * 
	 * @param name
	 * @return
	 */
	public RedisLock getLock(String name) {
		return new RedisLock(this, name);
	}

	/**
	 * 获得一把异步锁
	 * 
	 * @param name
	 * @return
	 */
	public AsyncRedisLock getAsyncLock(String name) {
		return new AsyncRedisLock(this, name);
	}
}