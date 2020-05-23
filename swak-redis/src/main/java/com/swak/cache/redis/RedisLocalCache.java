package com.swak.cache.redis;

import java.lang.reflect.Array;

import org.ehcache.core.EhcacheBase;
import org.springframework.beans.factory.DisposableBean;

import com.swak.cache.Entity;
import com.swak.cache.LocalCache;
import com.swak.cache.SafeEncoder;
import com.swak.pubsub.RedisPubSubHandler;
import com.swak.serializer.SerializationUtils;
import com.swak.utils.Sets;

/**
 * 本地缓存
 * 
 * @author root
 */
public class RedisLocalCache extends RedisPubSubHandler implements LocalCache<Object>, DisposableBean {

	private final String LOCAL_CACHE_TOPIC = "LOCAL_CACHE_TOPIC";
	private final EhcacheBase<String, byte[]> cache;
	private final String name;

	public RedisLocalCache(String name, EhcacheBase<String, byte[]> local) {
		this.name = name;
		this.cache = local;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Object getNativeCache() {
		return this.cache;
	}

	@Override
	public Entity<Object> putObject(String key, Object value) {
		this.cache.put(key, SerializationUtils.serialize(value));
		return new Entity<Object>(key, value);
	}

	@Override
	public Long delete(String key) {
		this.cache.remove(key);
		return 1L;
	}

	@Override
	public Long delete(String... keys) {
		if (keys != null && keys.length != 0) {
			this.cache.removeAll(Sets.newHashSet(keys));
		}
		return keys != null ? (long) keys.length : 0;
	}

	@Override
	public Boolean exists(String key) {
		return this.cache.containsKey(key);
	}

	@Override
	public Object getObject(String key) {
		byte[] value = this.cache.get(key);
		return SerializationUtils.deserialize(value);
	}

	@Override
	public String getString(String key) {
		byte[] value = this.cache.get(key);
		return SafeEncoder.encode(value);
	}

	@Override
	public Entity<String> putString(String key, String value) {
		this.cache.put(key, SafeEncoder.encode(value));
		return new Entity<String>(key, value);
	}

	@Override
	public Long ttl(String key) {
		return -2L;
	}

	// ------------ 初始化 - 销毁 ----------
	@Override
	public void destroy() throws Exception {
	}

	// ------------- 消息订阅 -------------
	@Override
	public String getChannel() {
		return LOCAL_CACHE_TOPIC;
	}

	@Override
	public void onMessage(String channel, byte[] message) {
		if (channel.equals(LOCAL_CACHE_TOPIC)) {
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
		if (key instanceof Array) {
            cache.removeAll(Sets.newHashSet((String[]) key));
        } else {
            cache.remove((String) key);
        }
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
	@Override
    public void sendEvictCmd(Object key) {
		if (key != null) {
			// 发送广播
			Command cmd = new Command(Command.OPT_DELETE_KEY, key);
			this.publish(LOCAL_CACHE_TOPIC, cmd.toBuffers());
		}
	}
}