package com.swak.vertx.security.jwt;

import java.util.Map;

import com.swak.utils.JsonMapper;

/**
 * jwt 数据操作
 * 
 * @author lifeng
 */
public abstract class JWTObject {

	/**
	 * 真实的存储数据
	 */
	protected Map<String, Object> map;

	/**
	 * 获取数据
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(String key) {
		return (T) map.get(key);
	}

	/**
	 * 获取数据
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(String key, Object defaultValue) {
		Object value = map.get(key);
		return (T)(value == null ? defaultValue : value);
	}

	/**
	 * 添加项目
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public <T extends JWTObject> T put(String key, Object value) {
		map.put(key, value);
		return as();
	}
	
	/**
	 * 是否包含属性
	 * 
	 * @param key
	 * @return
	 */
	public Boolean containsKey(String key) {
		return this.map.containsKey(key);
	}

	/**
	 * Encode this JSON object as a string.
	 *
	 * @return the string encoding.
	 */
	public String encode() {
		return JsonMapper.toJson(this.map);
	}

	/**
	 * 当前的对象
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends JWTObject> T as() {
		return (T) this;
	}

	/**
	 * 实际的数据
	 * @return
	 */
	public Map<String, Object> getData() {
		return map;
	}
}
