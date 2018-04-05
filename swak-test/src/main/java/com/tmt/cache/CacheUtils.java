package com.tmt.cache;

import com.swak.common.cache.Cache;
import com.swak.common.cache.CacheManager;
import com.swak.common.utils.SpringContextHolder;

/**
 * 缓存工具
 * @author lifeng
 */
public final class CacheUtils {

	  private static CacheManager cacheManager;
	  
	  static {
		  cacheManager = SpringContextHolder.getBean(CacheManager.class);
	  }
	  
	  /**
	   * 包装为二级缓存
	   * @param cache
	   * @return
	   */
	  public static <T> Cache<T> wrap(Cache<T> cache) {
		  return cacheManager.wrap(cache);
	  }
	  
	  /**
	   * 构建一个 sys 缓存
	   * @return
	   */
	  public static Builder sys() {
		  Cache<Object> cache = cacheManager.getCache("sys");
		  return new Builder(cache);
	  }
	  
	  public static class Builder {
		  private Cache<Object> cache;
		  private Cache<Object> wrap;
		  public Builder(Cache<Object> cache) {
			  this.cache = cache;
		  }
		  public Builder wrap() {
			  wrap = CacheUtils.wrap(cache);
			  return this;
		  }
		  public Builder expire(int seconds) {
			  cache.setTimeToIdle(seconds);
			  return this;
		  }
		  public Cache<Object> get() {
			  return wrap != null ? wrap: cache;
		  }
	  }
}