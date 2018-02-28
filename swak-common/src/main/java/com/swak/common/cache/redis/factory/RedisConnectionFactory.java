package com.swak.common.cache.redis.factory;

import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * jedis 连接工厂
 * @author lifeng
 */
public class RedisConnectionFactory implements InitializingBean {

	private String hosts = Protocol.DEFAULT_HOST.concat(":").concat(String.valueOf(Protocol.DEFAULT_PORT));
	private int timeout = Protocol.DEFAULT_TIMEOUT;
	private String password;
	private boolean enabledCluster = false;
	private JedisPoolConfig poolConfig = new JedisPoolConfig();
	private IRedisCacheUtils redisCache; // 真实提供的服务
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (enabledCluster) {
			redisCache = new RedisClusterCacheUtils(poolConfig, hosts, password, timeout);
		} else {
			redisCache = new RedisCacheUtils(poolConfig, hosts, password, timeout);
		}
	}
	
	/**
	 * 获得redis操作入口
	 * @return
	 */
	public IRedisCacheUtils getRedisCache() {
		return redisCache;
	}
	public String getHosts() {
		return hosts;
	}
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isEnabledCluster() {
		return enabledCluster;
	}
	public void setEnabledCluster(boolean enabledCluster) {
		this.enabledCluster = enabledCluster;
	}
	public JedisPoolConfig getPoolConfig() {
		return poolConfig;
	}
	public void setPoolConfig(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}
}