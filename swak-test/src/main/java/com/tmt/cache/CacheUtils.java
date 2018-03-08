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
	   * 获得一个指定名称的缓存
	   * @param name
	   * @return
	   */
	  public static Cache getSysCache() {
		  return cacheManager.getCache("sys");
	  }
	  
	  /**
	   * 包装为二级缓存
	   * @param cache
	   * @return
	   */
	  public static Cache wrap(Cache cache) {
		  return cacheManager.wrap(cache);
	  }
	  
	  /**
	   * 构建一个 sys 缓存
	   * @return
	   */
	  public static Builder sys() {
		  Cache cache = cacheManager.getCache("sys");
		  return new Builder(cache);
	  }
	  
	  public static class Builder {
		  private Cache cache;
		  public Builder(Cache cache) {
			  this.cache = cache;
		  }
		  public Builder wrap() {
			  cache = CacheUtils.wrap(cache);
			  return this;
		  }
		  public Cache get() {
			  return cache;
		  }
	  }
}