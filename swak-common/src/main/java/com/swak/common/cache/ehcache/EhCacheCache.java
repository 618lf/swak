package com.swak.common.cache.ehcache;

import com.swak.common.cache.Cache;
import com.swak.common.utils.Lists;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class EhCacheCache implements Cache<Object> {

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
	public void putObject(String key, Object value) {
		this.cache.put(new Element(key, value));
	}

	@Override
	public void delete(String key){
		this.cache.remove(key);
	}
	
	@Override
	public void delete(String ... keys) {
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
		return  (element != null ? (element.getObjectValue()) : null);
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

	@Override
	public String getString(String key) {
		Element element = this.cache.get(key);
		return  (element != null ? (String)(element.getObjectValue()) : null);
	}

	@Override
	public void putString(String key, String value) {
		this.cache.put(new Element(key, value));
	}
}