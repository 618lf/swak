package com.swak.cache.redis.policy;

import java.time.Duration;
import java.util.function.Supplier;

import org.ehcache.expiry.ExpiryPolicy;

/**
 * 过期策略
 * 
 * @author lifeng
 */
public final class ExpiryPolicys {

	/**
	 * 创建固定期限的过期策略
	 * 
	 * @return
	 */
	public static ExpiryPolicy<String, byte[]> fixedExpiryPolicy(final Duration seconds) {
		return new ExpiryPolicy<String, byte[]>() {
			@Override
			public Duration getExpiryForCreation(String key, byte[] value) {
				return seconds;
			}

			@Override
			public Duration getExpiryForAccess(String key, Supplier<? extends byte[]> value) {
				return null;
			}

			@Override
			public Duration getExpiryForUpdate(String key, Supplier<? extends byte[]> oldValue, byte[] newValue) {
				return null;
			}
		};
	}
}
