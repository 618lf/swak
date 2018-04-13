package com.swak.common.persistence.incrementer;

import java.io.Serializable;
import java.util.UUID;

/**
 * UUID 
 * @author liFeng
 * 2014年6月9日
 */
public class UUIdGenerator implements IdGenerator{

	@Override
	public Serializable generateId() {
		return uuid();
	}
	
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
