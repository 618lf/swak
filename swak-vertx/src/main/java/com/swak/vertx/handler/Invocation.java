package com.swak.vertx.handler;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import com.swak.Constants;
import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.InvokerAddress;
import com.swak.vertx.transport.VertxProxy;
import com.swak.vertx.transport.codec.Msg;

/**
 * 消息的异步处理
 * 
 * @author lifeng
 */
public interface Invocation {

	/**
	 * 通过类型解析出服务地址
	 * 
	 * @param type
	 * @return
	 */
	default String getAddress(Class<?> type) {
		// 访问的地址
		String address = StringUtils.EMPTY;

		// 定义的访问的地址
		InvokerAddress invokerAddress = type.getAnnotation(InvokerAddress.class);
		if (invokerAddress != null) {
			address = invokerAddress.value();
		}

		// 默认使用接口的全额限定名称
		if (StringUtils.isBlank(address)) {
			address = type.getName();
		}

		// 约定去掉后面的 Asyncx
		return StringUtils.substringBeforeLast(address, Constants._ASYNC_SUFFIX);
	}

	/**
	 * 消息的异步处理
	 * 
	 * @param vertx
	 * @param address
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	default CompletableFuture<Object> invoke(VertxProxy vertx, String address, Method method, Object[] args)
			throws Throwable {

		// 获取方法缓存
		MethodMeta meta = MethodCache.get(method);

		// 异步future
		CompletableFuture<Object> future = new CompletableFuture<Object>();

		// 构建请求消息
		Msg request = new Msg(meta, args);

		// 发送消息，处理相应结果
		vertx.sentMessage(address, request, meta.getTimeOut(), res -> {

			// 约定的通讯协议
			Msg result = (Msg) res.result().body();

			// 错误处理 - 结果返回
			Throwable result_error = result.getError();
			Object result_result = result.getResult();

			// 自动生成异步接口返回值
			if (meta.getNestedReturnType() == Msg.class) {
				result_result = result;
			}

			// 优先错误处理
			if (result_error != null) {
				future.completeExceptionally(result_error);
			}

			// 结果返回
			else {
				future.complete(result_result);
			}
		});

		// 返回异步future， futrue收到消息后会触发下一步的操作
		return future;
	}
}
