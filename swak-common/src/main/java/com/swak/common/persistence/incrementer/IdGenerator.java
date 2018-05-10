package com.swak.common.persistence.incrementer;

/**
 * ID 生成器
 * @author root
 */
public interface IdGenerator{
	
	/**
	 * 提供服务
	 * @return
	 */
	<T> T id();
}