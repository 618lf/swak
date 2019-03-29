package com.swak.cache;

/**
 * 声明本地缓存
 */
public interface LocalCache<T> extends Cache<T> {

	/**
	 * 发送删除Key的命令
	 * 
	 * @param key
	 */
	void sendEvictCmd(Object key);
}
