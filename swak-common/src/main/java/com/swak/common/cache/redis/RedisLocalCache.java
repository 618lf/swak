package com.swak.common.cache.redis;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.swak.common.cache.Cache;
import com.swak.common.cache.ehcache.EhCacheCache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.util.SafeEncoder;

/**
 * 本地缓存
 * 
 * @author root
 */
public class RedisLocalCache extends BinaryJedisPubSub implements Cache, InitializingBean, DisposableBean {

	private CacheManager cacheManager;
	
	// 本地缓存
	private EhCacheCache local;

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Ehcache ehcache = cacheManager.getEhcache("local");
		this.local = new EhCacheCache(ehcache);
	}

	@Override
	public void destroy() throws Exception {
		cacheManager.shutdown();
	}

	/**
	 * 订阅的频道
	 * 
	 * @return
	 */
	public byte[] getChannels() {
		return SafeEncoder.encode("local_channel");
	}

	@Override
	public String getName() {
		return local.getName();
	}

	@Override
	public Object getNativeCache() {
		return local.getNativeCache();
	}

	@Override
	public <T> T get(String key) {
		return local.get(key);
	}

	@Override
	public void delete(String key) {
		local.delete(key);
	}

	@Override
	public void delete(List<String> keys) {
		local.delete(keys);
	}

	@Override
	public boolean exists(String key) {
		return local.exists(key);
	}

	@Override
	public void put(String key, Object value) {
		local.put(key, value);
	}
	
	@Override
	public void clear() {
		local.clear();
	}

	// ------------- 消息订阅 -------------
	/**
	 * 获取订阅的消息
	 */
	@Override
	public void onMessage(byte[] channel, byte[] message) {

		// 无效消息
		if (message != null && message.length <= 0) {
			return;
		}

		try {
			Command cmd = Command.parse(message);
			if (cmd == null || cmd.isLocalCommand()) {
				return;
			}
			switch (cmd.getOperator()) {
			case Command.OPT_DELETE_KEY:
				onDeleteCacheKey(cmd.getKey());
				break;
			case Command.OPT_CLEAR_KEY:
				onClearCacheKey();
				break;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 删除一级缓存的键对应内容
	 * 
	 * @param region
	 *            : Cache region name
	 * @param key
	 *            : cache key
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void onDeleteCacheKey(Object key) {
		if (key instanceof List)
			local.delete((List) key);
		else
			local.delete((String)key);
	}

	/**
	 * 清除一级缓存的键对应内容
	 * 
	 * @param region
	 *            Cache region name
	 */
	protected void onClearCacheKey() {
		local.clear();
	}

	@Override
	public long ttl(String key) {
		Element element = local.getNativeCache().get(key);
		if (element != null && element.getTimeToLive() != 0) {
			return (element.getExpirationTime() - System.currentTimeMillis()) / 1000;
		}
		return -2;
	}
}