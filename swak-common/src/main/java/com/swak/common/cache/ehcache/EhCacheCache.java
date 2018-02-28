package com.swak.common.cache.ehcache;

import java.util.List;

import com.swak.common.cache.Cache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class EhCacheCache implements Cache {

	private final Ehcache cache;

	/**
	 * Create an {@link EhCacheCache} instance.
	 * @param ehcache backing Ehcache instance
	 */
	public EhCacheCache(Ehcache ehcache) {
		this.cache = ehcache;
	}

	@Override
	public String getName() {
		return this.cache.getName();
	}

	@Override
	public Ehcache getNativeCache() {
		return this.cache;
	}

	@Override
	public void put(String key, Object value) {
		this.cache.put(new Element(key, value));
	}

	@Override
	public void delete(String key){
		this.cache.remove(key);
	}
	
	@Override
	public void delete(List<String> keys) {
		if (keys != null && keys.size() != 0) {
			this.cache.removeAll(keys);
		}
	}
	
	@Override
	public boolean exists(String key) {
		return this.cache.isKeyInCache(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		Element element = this.cache.get(key);
		return  (element != null ? (T)(element.getObjectValue()) : null);
	}
	
	@Override
	public void clear() {
		this.cache.removeAll();
	}

	@Override
	public long ttl(String key) {
		Element element = this.cache.get(key);
		return (element != null ? element.getTimeToIdle(): -2);
	}
}