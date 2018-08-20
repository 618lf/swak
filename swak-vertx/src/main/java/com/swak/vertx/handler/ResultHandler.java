package com.swak.vertx.handler;

import java.util.List;

import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.vertx.handler.converter.HttpMessageConverter;

import io.vertx.ext.web.RoutingContext;

/**
 * 处理结果
 * 
 * @author lifeng
 */
public class ResultHandler {

	private List<HttpMessageConverter> converters = Lists.newArrayList();

	/**
	 * 添加转换器
	 * 
	 * @param converter
	 * @return
	 */
	public ResultHandler addConverter(HttpMessageConverter converter) {
		converters.add(converter);
		return this;
	}

	/**
	 * 处理结果
	 * 
	 * @param result
	 * @return
	 */
	public void handlResult(Object result, Throwable e, RoutingContext context) {

		// 通过转换器输出
		if (result != null) {
			for (HttpMessageConverter converter : converters) {
				if (converter.canWrite(result.getClass())) {
					converter.write(result, context.response());
					return;
				}
			}
		}

		// 托底输出
		context.response().end(StringUtils.EMPTY);
	}

	/**
	 * 处理结果
	 * 
	 * @param result
	 * @return
	 */
	public void handlError(Throwable e, RoutingContext context) {
		context.response().end(e.getMessage());
	}
}