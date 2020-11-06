package com.swak.config.customizer;

/**
 * 配置器
 * 
 * @author lifeng
 * @date 2020年11月6日 下午12:55:02
 */
public interface Customizer<T> {

	/**
	 * 配置
	 * 
	 * @param t
	 */
	void customize(T t);
}
