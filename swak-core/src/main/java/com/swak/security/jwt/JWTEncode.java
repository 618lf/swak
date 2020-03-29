package com.swak.security.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.swak.utils.JsonMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于Json 的转换
 * 
 * @author lifeng
 */
public class JWTEncode {

	/**
	 * Encode this Map as a JSON object.
	 *
	 * @return the string encoding.
	 */
	public static String encode(Map<String, Object> map) {
		try (SerializeWriter out = new SerializeWriter(null, JSON.DEFAULT_GENERATE_FEATURE, JsonMapper.FEATURES)) {
			JSONSerializer serializer = new JSONSerializer(out);
			serializer.write(map);
			return out.toString();
		}
	}

	/**
	 * Encode this JSON object as a Map.
	 *
	 * @return the string encoding.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> encode(String json) {
		return JSON.parseObject(json, HashMap.class);
	}
}