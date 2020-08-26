package com.swak.vertx.protocol.im;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.Constants;
import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.vertx.protocol.im.converter.HttpMessageConverter;
import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * 处理结果
 *
 * @author: lifeng
 * @date: 2020/3/29 20:16
 */
public class ResultHandler {

	private Logger logger = LoggerFactory.getLogger(ResultHandler.class);

	private List<HttpMessageConverter> converters = Lists.newArrayList();

	/**
	 * 添加转换器
	 *
	 * @param converter 转换器
	 */
	public void addConverter(HttpMessageConverter converter) {
		converters.add(converter);
	}

	/**
	 * 处理结果
	 *
	 * @param result  结果
	 * @param e       异常
	 * @param context 请求上下文
	 */
	public void handleResult(Object result, Throwable e, ImContext context) {

		// 如果有异常
		if (e != null) {
			this.handleError(e, context);
			return;
		}

		// 已经输出数据
		if (!this.canWrite(context)) {
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
			this.handleError(ex, context);
			return;
		}

		// 允许返回 void 但需自己实现输出
		if (this.canWrite(context)) {
			context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
			context.response().out(Result.success(StringUtils.EMPTY).toJson());
		}
	}

	/**
	 * 处理错误
	 *
	 * @param e       异常
	 * @param context 请求上下文
	 */
	public void handleError(Throwable e, ImContext context) {

		// 已经输出数据
		if (!this.canWrite(context)) {
			return;
		}

		// 输出错误信息
		context.put(Constants.EXCEPTION_NAME, e);
		context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		context.response().out(Result.error(ErrorCode.SERVER_ERROR).toJson());

		// 打印错误信息
		logger.error("{}", context.request().uri(), e.getCause() != null ? e.getCause() : e);
	}

	/**
	 * 是否可以输出数据
	 */
	private boolean canWrite(ImContext context) {
		return !context.closed();
	}
}