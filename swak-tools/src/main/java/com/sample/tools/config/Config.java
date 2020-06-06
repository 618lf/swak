package com.sample.tools.config;

import java.util.HashMap;

/**
 * 系统配置
 * 
 * @author lifeng
 */
public class Config extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public Config string(String key, String value) {
		this.put(key, value);
		return this;
	}

	public Config integer(String key, int value) {
		this.put(key, value);
		return this;
	}

	public String string(String key) {
		return (String) this.get(key);
	}

	public Integer integer(String key) {
		return (Integer) this.get(key);
	}
}