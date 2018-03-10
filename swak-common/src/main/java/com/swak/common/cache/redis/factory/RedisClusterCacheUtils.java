package com.swak.common.cache.redis.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.swak.common.utils.StringUtils;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;

/**
 * redis 集群
 * @author root
 */
public class RedisClusterCacheUtils implements IRedisCacheUtils{

	protected JedisCluster jedisCluster;
	protected int numRetries = 30;
	
	public RedisClusterCacheUtils(JedisPoolConfig poolConfig, String hosts, String password, int timeout) {
		if (!StringUtils.hasText(password)) {
			jedisCluster = new JedisCluster(getJedisClusterNodesSet(hosts), timeout, poolConfig);
		} else {
			// 使用了默认的 JedisCluster.DEFAULT_MAX_REDIRECTIONS
			jedisCluster = new JedisCluster(getJedisClusterNodesSet(hosts), timeout, timeout, 5, password, poolConfig);
		}
	}
	
	private Set<HostAndPort> getJedisClusterNodesSet(String hosts) {
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		hosts = hosts.replaceAll("\\s", "");
		String[] hostPorts = hosts.split(",");
		for(String hostPort : hostPorts) {
			String[] hostPortArr = hostPort.split(":");
			nodes.add(new HostAndPort(hostPortArr[0], Integer.valueOf(hostPortArr[1])));
		}
		return nodes;
	}
	
	/**
	 * 订阅
	 */
	@Override
	public void subscribe(BinaryJedisPubSub o, byte[] channels) {
		JedisCluster jedis = this.getJedis();
		jedis.subscribe(o, channels);
	}
	
	/**
	 * 发布
	 */
	@Override
	public void publish(byte[] channel, byte[] message) {
		JedisCluster jedis = this.getJedis();
		jedis.publish(channel, message);
	}

	/**
	 * 得到链接
	 * @return
	 */
	public JedisCluster getJedis() {
		return jedisCluster;
	}
	
	/**
	 * 等待失败处理
	 */
	protected void waitforFailover() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void add(String key, byte[] value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void set(String key, byte[] value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void set(String key, byte[] value, int seconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long delete(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long delete(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletes(String pattern) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long size(String pattern) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> keys(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<byte[]> gets(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long expire(String key, int seconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, byte[]> keyValues(String pattern, String prex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long ttl(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void scan(String pattern, ScanParams params, ScanCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean hSet(String key, String field, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean hSetNX(String key, String field, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hDel(String key, String... fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean hExists(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] hGet(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, byte[]> hGetAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> hKeys(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hLen(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<byte[]> hMGet(String key, String... fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hMSet(String key, Map<String, byte[]> tuple) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<byte[]> hVals(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long lPush(String key, byte[]... value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] lPop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long rPush(String key, byte[]... value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] rPop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long lLen(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run(String script, byte[]... values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T runAndGetOne(String script, byte[]... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> runAndGetList(String script, byte[]... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] invoke(Callback call) {
		// TODO Auto-generated method stub
		return null;
	}
}