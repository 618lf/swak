package com.swak.redis;

import java.util.List;

import com.swak.cache.Cache;
import com.swak.cache.Entity;
import com.swak.cache.LocalCache;
import com.swak.utils.Lists;

/**
 * 如果本地缓存还有，但是远程缓存删除了--怎么处理 二级缓存功能
 * 
 * @author root
 */
public class RedisCacheChannel<T> implements Cache<T> {

	private final LocalCache<Object> local;
	private final RedisCache<T> remote;

	public RedisCacheChannel(RedisCache<T> remote, LocalCache<Object> local) {
		this.remote = remote;
		this.local = local;
	}

	/**
	 * 远程缓存的命名规则
	 * 
	 * @param key
	 * @return
	 */
	protected String getKeyName(String key) {
		return remote.getKeyName(key);
	}

	// 不使用本地缓存的方法
	@Override
	public String getName() {
		return remote.getName();
	}

	@Override
	public Object getNativeCache() {
		return remote.getNativeCache();
	}

	@Override
	public Boolean exists(String key) {
		return remote.exists(key);
	}

	@Override
	public Long ttl(String key) {
		return remote.ttl(key);
	}

	// 以下实现了使用本地缓存的方法
	/**
	 * 优先从1级缓存获取数据。
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T getObject(String key) {
		String localKey = this.getKeyName(key);
		Object t = local.getObject(localKey);
		if (t == null) {
			T o = remote.getObject(key);
			if (o != null) {
				t = o;
				local.putObject(localKey, t);
			}
		}
		return (T) t;
	}

	/**
	 * 优先从1级缓存获取数据。
	 */
	@Override
	public String getString(String key) {
		String localKey = this.getKeyName(key);
		String t = local.getString(localKey);
		if (t == null) {
			String o = remote.getString(key);
			if (o != null) {
				t = o;
				local.putString(localKey, t);
			}
		}
		return t;
	}

	@Override
	public Entity<T> putObject(String key, T value) {
		String localKey = this.getKeyName(key);
		_sendEvictCmd(localKey);// 清除原有的一级缓存的内容
		local.putObject(localKey, value);
		return remote.putObject(key, value);
	}

	@Override
	public Entity<String> putString(String key, String value) {
		String localKey = this.getKeyName(key);
		_sendEvictCmd(localKey);// 清除原有的一级缓存的内容
		local.putString(localKey, value);
		return remote.putString(key, value);
	}

	@Override
	public Long delete(String key) {
		String localKey = this.getKeyName(key);
		local.delete(localKey);
		_sendEvictCmd(localKey);
		return remote.delete(key);
	}

	@Override
	public Long delete(String... keys) {
		List<String> _keys = this._delete(keys);
		if (_keys != null && _keys.size() != 0) {
			_sendEvictCmd(_keys);
		}
		return remote.delete(keys);
	}

	// 返回整理好的keys
	private List<String> _delete(String... keys) {
		if (keys != null && keys.length != 0) {
			List<String> _keys = Lists.newArrayList();
			for (String key : keys) {
				String _key = this.getKeyName(key);
				_keys.add(_key);
				local.delete(_key);
			}
			return _keys;
		}
		return null;
	}

	/**
	 * 发送清除缓存的广播命令
	 * 
	 * @param region
	 *            : Cache region name
	 * @param key
	 *            : cache key
	 */
	private void _sendEvictCmd(Object key) {
		local.sendEvictCmd(key);
	}
}