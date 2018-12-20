package com.swak.reactivex.web.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.utils.JsonMapper;

/**
 * 处理json
 * 出类其他转换器之外的情况
 * @author lifeng
 */
public class JsonHttpMessageConverter implements HttpMessageConverter {

	private FastJsonConfig fastJsonConfig = new FastJsonConfig();

	@Override
	public boolean canWrite(Class<?> clazz) {
		return clazz != void.class;
	}

	@Override
	public void write(Object obj, HttpServerResponse response) {
		try {
			JsonMapper.writeJSONString(response.json().getOutputStream(), fastJsonConfig.getCharset(), obj, fastJsonConfig.getSerializeConfig(),
					fastJsonConfig.getSerializeFilters(), fastJsonConfig.getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE,
					fastJsonConfig.getSerializerFeatures());
		} catch (Exception e) {}
	}
}