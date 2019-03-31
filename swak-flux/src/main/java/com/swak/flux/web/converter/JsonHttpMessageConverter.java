package com.swak.flux.web.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.utils.JsonMapper;

/**
 * 处理json 出类其他转换器之外的情况
 * 
 * @author lifeng
 */
public class JsonHttpMessageConverter implements HttpMessageConverter {

	private SerializerFeature[] features = new SerializerFeature[] {SerializerFeature.DisableCircularReferenceDetect};

	@Override
	public boolean canWrite(Class<?> clazz) {
		return clazz != void.class;
	}

	@Override
	public void write(Object obj, HttpServerResponse response) {
		String content = JsonMapper.toJSONString(obj, JSON.DEFAULT_GENERATE_FEATURE, features);
		response.json().buffer(content);
	}
}