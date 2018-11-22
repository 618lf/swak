package com.swak.vertx.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.exception.BaseRuntimeException;
import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.InvokerAddress;
import com.swak.vertx.transport.codec.Msg;

/**
 * 调用执行器
 * 
 * @author lifeng
 */
public class InvokerHandler implements InvocationHandler {

	private final Class<?> type;
	private final String address;
	private final VertxHandler vertx;

	public InvokerHandler(VertxHandler vertx, Class<?> type) {
		this.vertx = vertx;
		this.type = type;
		this.address = this.initAddress();
		this.initMethods();
	}

	private String initAddress() {

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

		// 约定去掉后面的 Async
		return StringUtils.substringBeforeLast(address, "Async");
	}

	private void initMethods() {
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			MethodCache.set(method);
		}
	}

	/**
	 * 只支持异步接口的调用
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MethodMeta meta = MethodCache.get(method);
		
		// 异步future
		CompletableFuture<Object> future = new CompletableFuture<Object>();
		
		// 构建请求消息
		Msg request = new Msg(meta, args);
		
		// 发送消息，处理相应结果
		vertx.sentMessage(this.address, request, meta.getTimeOut(), res -> {
			
			// 约定的通讯协议
			Msg result = (Msg) res.result().body();
			
			// 错误处理 - 结果返回
			String result_error = result.getError();
			Object result_result = result.getResult();
			
			// 自动生成异步接口返回值
			if (meta.getNestedReturnType() == Msg.class) {
				result_result = result;
			}
			
			// 优先错误处理
			if (StringUtils.isNotBlank(result_error)) {
				future.completeExceptionally(new BaseRuntimeException(result_error));
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
