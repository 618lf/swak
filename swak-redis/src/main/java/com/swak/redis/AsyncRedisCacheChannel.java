package com.swak.redis;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.cache.AsyncCache;
import com.swak.cache.Entity;
import com.swak.cache.LocalCache;
import com.swak.utils.Lists;

/**
 * 异步缓存支持二级缓存
 * 
 * @author lifeng
 *
 * @param <T>
 */
public class AsyncRedisCacheChannel<T> implements AsyncCache<T> {

	private final LocalCache<Object> local;
	private final AsyncRedisCache<T> remote;

	public AsyncRedisCacheChannel(AsyncRedisCache<T> remote, LocalCache<Object> local) {
		this.remote = remote;
		this.local = local;
	}

	@Override
	public String getName() {
		return this.remote.getName();
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

	@Override
	@SuppressWarnings("unchecked")
	public CompletionStage<T> getObject(String key) {
		String localKey = this.getKeyName(key);
		Object t = local.getObject(localKey);
		if (t == null) {
			return remote.getObject(key).thenApply(v -> {
				if (v != null) {
					local.putObject(localKey, t);
				}
				return v;
			});
		}
		return CompletableFuture.completedFuture((T) t);
	}

	@Override
	public CompletionStage<String> getString(String key) {
		String localKey = this.getKeyName(key);
		String t = local.getString(localKey);
		if (t == null) {
			return remote.getString(key).thenApply(v -> {
				if (v != null) {
					local.putObject(localKey, t);
				}
				return v;
			});
		}
		return CompletableFuture.completedFuture(t);
	}

	@Override
	public CompletionStage<T> getObjectAndDel(String key) {
		return remote.getObjectAndDel(key).thenApply(res ->{
			String localKey = this.getKeyName(key);
			local.delete(localKey);
			_sendEvictCmd(localKey);
			return res;
		});
	}

	@Override
	public CompletionStage<String> getStringAndDel(String key) {
		return remote.getStringAndDel(key).thenApply(res ->{
			String localKey = this.getKeyName(key);
			local.delete(localKey);
			_sendEvictCmd(localKey);
			return res;
		});
	}

	@Override
	public CompletionStage<Long> delete(String key) {
		String localKey = this.getKeyName(key);
		local.delete(localKey);
		_sendEvictCmd(localKey);
		return remote.delete(localKey);
	}

	@Override
	public CompletionStage<Long> delete(String... keys) {
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

	@Override
	public CompletionStage<Long> exists(String key) {
		return remote.exists(key);
	}

	@Override
	public CompletionStage<Entity<T>> putObject(String key, T value) {
		String localKey = this.getKeyName(key);
		_sendEvictCmd(localKey);
		local.putObject(localKey, value);
		return remote.putObject(key, value);
	}

	@Override
	public CompletionStage<Entity<String>> putString(String key, String value) {
		String localKey = this.getKeyName(key);
		_sendEvictCmd(localKey);
		local.putString(localKey, value);
		return remote.putString(key, value);
	}

	@Override
	public CompletionStage<Long> ttl(String key) {
		return remote.ttl(key);
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