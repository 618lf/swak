package com.swak.common.persistence.incrementer;

import java.io.Serializable;

/**
 * ID 生成器
 * @author root
 */
public interface IdGenerator{
	
	/**
	 * 提供服务
	 * @return
	 */
	Serializable generateId();
}