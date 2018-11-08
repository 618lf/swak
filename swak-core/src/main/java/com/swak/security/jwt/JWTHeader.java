package com.swak.security.jwt;

import java.util.HashMap;

import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;

/**
 * JWT 的头部设置
 * 
 * @author lifeng
 */
public class JWTHeader extends JWTObject {
	
	public JWTHeader() {
		this.map = Maps.newHashMap();
	}
	
	@SuppressWarnings("unchecked")
	public JWTHeader(String json) {
		this.map = JsonMapper.fromJson(json, HashMap.class);
	}
	
	/**
	 * 合并
	 * @param header
	 * @return
	 */
	public JWTHeader merge(JWTHeader header) {
		this.map.putAll(header.map);
		return this;
	}
}