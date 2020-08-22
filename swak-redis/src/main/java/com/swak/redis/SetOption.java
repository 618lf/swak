package com.swak.redis;

/**
 * Set 的属性
 * 
 * @author lifeng
 * @date 2020年8月20日 上午9:47:31
 */
public enum SetOption {
	
	/**
	 * Do not set any additional command argument.
	 *
	 * @return
	 */
	UPSERT,

	/**
	 * {@code NX}
	 *
	 * @return
	 */
	SET_IF_ABSENT,

	/**
	 * {@code XX}
	 *
	 * @return
	 */
	SET_IF_PRESENT;

	/**
	 * Do not set any additional command argument.
	 *
	 * @return
	 */
	public static SetOption upsert() {
		return UPSERT;
	}

	/**
	 * {@code XX}
	 *
	 * @return
	 */
	public static SetOption ifPresent() {
		return SET_IF_PRESENT;
	}

	/**
	 * {@code NX}
	 *
	 * @return
	 */
	public static SetOption ifAbsent() {
		return SET_IF_ABSENT;
	}
}
