package com.swak.common.cache.redis.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.support.ConnectionPoolSupport;

/**
 * 池化处理
 * @author lifeng
 */
public class RedisConnectionPoolFactory implements RedisConnectionFactory<String, byte[]>{

	private final RedisClientDecorator client;
	private final GenericObjectPoolConfig poolConfig;
    private final Map<Class<?>, GenericObjectPool<StatefulConnection<String, byte[]>>> pools;
    private final Map<StatefulConnection<String, byte[]>, GenericObjectPool<StatefulConnection<String, byte[]>>> poolsRef;
    
	public RedisConnectionPoolFactory(RedisClientDecorator client, GenericObjectPoolConfig poolConfig) {
		this.client = client;
		this.poolConfig = poolConfig;
		pools = new ConcurrentHashMap<>(3);
		poolsRef = new ConcurrentHashMap<>(3);
	}

	/**
	 * 获取链接
	 */
	@Override
	public StatefulConnection<String, byte[]> getConnection(Class<?> connectionType) {
		try {
			
			GenericObjectPool<StatefulConnection<String, byte[]>> pool = pools.computeIfAbsent(connectionType, (poolType) ->{
				return ConnectionPoolSupport.createGenericObjectPool(()->{
					return client.getConnection(connectionType);
				}, poolConfig, false);
			});
			
			StatefulConnection<String, byte[]> connection = pool.borrowObject();

			poolsRef.put(connection, pool);
			
			return connection;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 关闭链接
	 */
	@Override
	public void destroy() throws Exception {
		if (!poolsRef.isEmpty()) {
			
			poolsRef.forEach((connection, pool) -> pool.returnObject(connection));
			poolsRef.clear();
		}
		pools.forEach((type, pool) -> pool.close());
		pools.clear();
	}
}