package com.swak.vertx.security.jwt;

import java.util.HashMap;

import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;

/**
 * JWT 的数据部分
 * 
 * @author lifeng
 */
public class JWTPayload extends JWTObject {

	public JWTPayload() {
		this.map = Maps.newHashMap();
	}

	@SuppressWarnings("unchecked")
	public JWTPayload(String json) {
		this.map = JsonMapper.fromJson(json, HashMap.class);
	}
}
