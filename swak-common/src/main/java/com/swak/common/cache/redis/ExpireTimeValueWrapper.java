package com.swak.common.cache.redis;

import java.io.Serializable;

/**
 * 保存了过期时间的的 ValueWrapper
 * @author root
 */
public class ExpireTimeValueWrapper implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Object value;// 值
	private int timeToLive;
	public ExpireTimeValueWrapper(){}
	public ExpireTimeValueWrapper(Object value, int timeToLive) {
		this.value = value;
		this.timeToLive = timeToLive;
	}
	public Object get() {
		return this.value;
	}
	public Object getValue() {
		return value;
	}
	public int getTimeToLive() {
		return timeToLive;
	}
	
	/**
	 * 设置的时间是有效的
	 * @param time
	 * @return
	 */
	public static boolean isValid(int time) {
		return time > 0;
	}
}
