package com.swak.vertx.handler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import com.swak.asm.Wrapper;
import com.swak.utils.Maps;
import com.swak.vertx.handler.codec.Msg;
import com.swak.vertx.utils.MethodCache;
import com.swak.vertx.utils.MethodCache.MethodMeta;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

/**
 * 自动创建这样一个 Verticle
 * 
 * @author lifeng
 */
public class ServiceHandler extends AbstractVerticle implements Handler<Message<Msg>> {

	private final Object service;
	private final String address;
	private final Class<?> type;
	private final Wrapper wrapper;
	private final Map<String, MethodMeta> methods;

	public ServiceHandler(Object service, Class<?> type) {
		this.service = service;
		this.type = type;
		this.address = type.getName();
		this.wrapper = Wrapper.getWrapper(type);
		this.methods = this.initMethods();
	}

	private Map<String, MethodMeta> initMethods() {
		Map<String, MethodMeta> methodMap = Maps.newHashMap();
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			MethodMeta meta = MethodCache.set(method);
			methodMap.put(meta.getMethodDesc(), meta);
		}
		return methodMap;
	}

	private MethodMeta lookupMethod(String methodDesc) {
		return methods.get(methodDesc);
	}

	/**
	 * 注册为消费者
	 */
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		this.getVertx().eventBus().<Msg>consumer(address).handler(this);
		startFuture.complete();
	}

	/**
	 * 处理消息
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void handle(Message<Msg> event) {
		Msg request = event.body();
		MethodMeta method = this.lookupMethod(request.getMethodDesc());
		Object result = null;
		Exception error = null;
		try {
			result = wrapper.invokeMethod(service, method.getMethodName(), method.getParameterTypes(),
					request.getArguments());
		} catch (Exception e) {
			error = e;
		}
		Msg response = request.reset();
		if (error != null) {
			response.setError(error.getMessage());
			event.reply(response);
		} else if (result != null && result instanceof CompletionStage) {
			CompletionStage<Object> resultFuture = (CompletionStage<Object>) result;
			resultFuture.whenComplete((r, e) -> {
				if (e == null) {
					response.setResult(r);
				} else {
					response.setError(e.getMessage());
				}
				event.reply(response);
			});
		} else {
			response.setError("please return CompletionStage");
			event.reply(response);
		}
	}
}