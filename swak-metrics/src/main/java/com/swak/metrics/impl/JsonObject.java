package com.swak.metrics.impl;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 简化json的操作
 * 
 * @author lifeng
 */
public class JsonObject {

	private JSONObject json;

	public JsonObject() {
		json = new JSONObject();
	}
	public JsonObject(Map<String, Object> map) {
		json = new JSONObject(map);
	}

	public JsonObject put(String key, Object value) {
		json.put(key, value);
		return this;
	}
}
