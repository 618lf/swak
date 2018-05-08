package com.swak.common.cache.redis;

import java.lang.reflect.Array;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.swak.common.cache.Cache;
import com.swak.common.cache.ehcache.EhCacheCache;
import com.swak.common.eventbus.RedisEventBus;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * 本地缓存
 * 
 * @author root
 */
public class RedisLocalCache extends RedisEventBus implements Cache<Object>, InitializingBean, DisposableBean {

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
	public String getChannels() {
		return "local_channel";
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
	public Object getObject(String key) {
		return local.getObject(key);
	}

	@Override
	public void delete(String key) {
		local.delete(key);
	}

	@Override
	public void delete(String ... keys) {
		local.delete(keys);
	}

	@Override
	public boolean exists(String key) {
		return local.exists(key);
	}

	@Override
	public void putObject(String key, Object value) {
		local.putObject(key, value);
	}
	
	@Override
	public String getString(String key) {
		return (String) local.getObject(key);
	}

	@Override
	public void putString(String key, String value) {
		local.putObject(key, value);
	}

	// ------------- 消息订阅 -------------
	/**
	 * 获取订阅的消息
	 */
	@Override
	public void message(byte[] channel, byte[] message) {

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
	protected void onDeleteCacheKey(Object key) {
		if (key instanceof Array)
			local.delete((String[]) key);
		else
			local.delete((String)key);
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