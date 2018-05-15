package com.swak.reactivex.web.converter;

import java.io.IOException;
import java.io.InputStream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.swak.common.utils.JsonMapper;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;

/**
 * 处理json
 * 出类其他转换器之外的情况
 * @author lifeng
 */
public class JsonHttpMessageConverter implements HttpMessageConverter<Object> {

	private FastJsonConfig fastJsonConfig = new FastJsonConfig();

	@Override
	public boolean canRead(Class<?> clazz) {
		return clazz != void.class;
	}

	@Override
	public Object read(Class<? extends Object> clazz, HttpServerRequest request) throws IOException {
		InputStream in = request.getInputStream();
		return JSON.parseObject(in, fastJsonConfig.getCharset(), clazz, fastJsonConfig.getFeatures());
	}

	@Override
	public boolean canWrite(Class<?> clazz) {
		return clazz != void.class;
	}

	@Override
	public void write(Object obj, HttpServerResponse response) throws IOException {
		JsonMapper.writeJSONString(response.json().getOutputStream(), fastJsonConfig.getCharset(), obj, fastJsonConfig.getSerializeConfig(),
				fastJsonConfig.getSerializeFilters(), fastJsonConfig.getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE,
				fastJsonConfig.getSerializerFeatures());
	}
}