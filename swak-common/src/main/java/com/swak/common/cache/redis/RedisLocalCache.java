package com.swak.common.cache.redis;

import java.lang.reflect.Array;

import org.springframework.beans.factory.DisposableBean;

import com.swak.common.Constants;
import com.swak.common.cache.Cache;
import com.swak.common.eventbus.Event;
import com.swak.common.eventbus.EventConsumer;
import com.swak.common.eventbus.EventProducer;
import com.swak.common.utils.Lists;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * 本地缓存
 * @author root
 */
public class RedisLocalCache implements Cache<Object>, EventConsumer, DisposableBean {

	private final Ehcache cache;
	private final CacheManager cacheManager;
	private EventProducer producer;

	public RedisLocalCache(CacheManager cacheManager, Ehcache local) {
		this.cacheManager = cacheManager;
		this.cache = local;
	}
	
	public void setProducer(EventProducer producer) {
		this.producer = producer;
	}

	@Override
	public String getName() {
		return cache.getName();
	}

	@Override
	public Object getNativeCache() {
		return this.cache;
	}

	@Override
	public void putObject(String key, Object value) {
		this.cache.put(new Element(key, value));
	}

	@Override
	public void delete(String key) {
		this.cache.remove(key);
	}

	@Override
	public void delete(String... keys) {
		if (keys != null && keys.length != 0) {
			this.cache.removeAll(Lists.newArrayList(keys));
		}
	}

	@Override
	public boolean exists(String key) {
		return this.cache.isKeyInCache(key);
	}

	@Override
	public Object getObject(String key) {
		Element element = this.cache.get(key);
		return (element != null ? (element.getObjectValue()) : null);
	}

	@Override
	public String getString(String key) {
		Element element = this.cache.get(key);
		return (element != null ? (String) (element.getObjectValue()) : null);
	}

	@Override
	public void putString(String key, String value) {
		this.cache.put(new Element(key, value));
	}

	@Override
	public long ttl(String key) {
		Element element = this.cache.get(key);
		if (element != null && element.getTimeToLive() != 0) {
			return (element.getExpirationTime() - System.currentTimeMillis()) / 1000;
		}
		return -2;
	}
	
	// ------------ 销毁 ----------

	@Override
	public void destroy() throws Exception {
		cacheManager.shutdown();
	}

	// ------------- 消息订阅 -------------

	@Override
	public String getChannel() {
		return Constants.LOCAL_CACHE_TOPIC;
	}

	/**
	 * 获取订阅的消息
	 */
	@Override
	public void onMessge(Event event) {

		byte[] message = event.getMessage();

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
			cache.remove((String[]) key);
		else
			cache.remove((String) key);
	}
	
	// --------------- 发布事件 ----------------
	/**
	 * 发送清除缓存的广播命令
	 * 
	 * @param region
	 *            : Cache region name
	 * @param key
	 *            : cache key
	 */
	public void sendEvictCmd(Object key) {
		if (key != null) {
			// 发送广播
			Command cmd = new Command(Command.OPT_DELETE_KEY, key);
			producer.publish(this.getChannel(), cmd.toBuffers());
		}
	}
}