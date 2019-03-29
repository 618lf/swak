package com.swak.cache.redis.factory;

import io.lettuce.core.api.StatefulConnection;

/**
 * 创建redis 的连接
 * 
 * @author lifeng
 */
public interface IRedisClient {
	
	/**
	 * 普通的连接
	 * @return
	 */
	<T extends StatefulConnection<byte[], byte[]>> T connect();

	/**
	 * 订阅发布
	 * @return
	 */
	<T extends StatefulConnection<byte[], byte[]>> T connectPubSub();
}
