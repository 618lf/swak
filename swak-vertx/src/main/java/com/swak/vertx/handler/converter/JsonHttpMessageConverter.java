package com.swak.vertx.handler.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.swak.utils.JsonMapper;
import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.http.HttpServerResponse;

/**
 * 处理json 出类其他转换器之外的情况
 * 
 * @author lifeng
 */
public class JsonHttpMessageConverter implements HttpMessageConverter {

	private SerializerFeature[] features = new SerializerFeature[] {};

	@Override
	public boolean canWrite(Class<?> clazz) {
		return clazz != void.class;
	}

	@Override
	public void write(Object obj, HttpServerResponse response) {
		response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		String content = JsonMapper.toJSONString(obj, JSON.DEFAULT_GENERATE_FEATURE, features);
		response.end(content);
	}
}