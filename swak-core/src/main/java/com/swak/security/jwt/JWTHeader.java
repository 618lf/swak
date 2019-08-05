package com.swak.security.jwt;

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

	public JWTHeader(String json) {
		this.map = JWTEncode.encode(json);
	}

	/**
	 * 合并
	 * 
	 * @param header
	 * @return
	 */
	public JWTHeader merge(JWTHeader header) {
		this.map.putAll(header.map);
		return this;
	}
}