package com.swak.redis;

/**
 * Redis 的操作模板
 * 
 * @author lifeng
 * @date 2020年8月19日 下午4:28:59
 */
public class RedisService {

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
}