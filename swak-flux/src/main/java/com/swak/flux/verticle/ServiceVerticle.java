package com.swak.flux.verticle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.ClassMeta;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.asm.Wrapper;
import com.swak.utils.ExceptionUtils;

/**
 * 将服务发布成为 verticle
 * 
 * @author lifeng
 */
public class ServiceVerticle implements Verticle {

	private static Logger logger = LoggerFactory.getLogger(ServiceVerticle.class);

	private final Object service;
	private final String address;
	@SuppressWarnings("unused")
	private final Class<?> type;
	private final Wrapper wrapper;
	private final ClassMeta classMeta;

	public ServiceVerticle(Object service, Class<?> type) {
		this.service = service;
		this.type = type;
		this.address = type.getName();
		this.wrapper = Wrapper.getWrapper(type);
		this.classMeta = MethodCache.set(type);
	}

	@Override
	public Msg handle(Msg request) {
		MethodMeta method = this.classMeta.lookup(request.getMethodDesc());
		Object result = null;
		Exception error = null;
		try {
			result = wrapper.invokeMethod(service, method.getMethodName(), method.getParameterTypes(),
					request.getArguments());
		} catch (Exception e) {
			error = e;
			logger.error("执行service错误", e);
		}

		// 响应
		Msg response = request.reset();

		// 错误消息
		if (error != null) {
			response.setError(ExceptionUtils.causedMessage(error));
		}

		// 可以不用异步接口
		else if (result != null) {
			response.setResult(result);
		}
		return response;
	}

	/**
	 * 唯一地址
	 */
	@Override
	public String address() {
		return address;
	}
}