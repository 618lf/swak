package com.swak.vertx.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.vertx.handler.converter.HttpMessageConverter;
import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.ext.web.RoutingContext;

/**
 * 处理结果
 * 
 * @author lifeng
 */
public class ResultHandler {

	private Logger logger = LoggerFactory.getLogger(ResultHandler.class);

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

		// 如果有异常
		if (e != null) {
			this.handlError(e, context);
			return;
		}

		// 已经输出数据
		if (context.response().ended()) {
			return;
		}

		try {
			// 通过转换器输出
			if (result != null) {
				for (HttpMessageConverter converter : converters) {
					if (converter.canWrite(result.getClass())) {
						converter.write(result, context.response());
						return;
					}
				}
			}
		} catch (Exception ex) {
			this.handlError(ex, context);
			return;
		}

		// 托底输出
		context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		context.response().end(Result.success(StringUtils.EMPTY).toJson());
	}

	/**
	 * 处理结果
	 * 
	 * @param result
	 * @return
	 */
	public void handlError(Throwable e, RoutingContext context) {

		// 已经输出数据
		if (context.response().ended()) {
			return;
		}

		// 输出错误信息
		context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		context.response().end(Result.error(ErrorCode.SERVER_ERROR).toJson());

		// 打印错误信息
		logger.error("输出结果异常:", e);
	}
}