package com.swak.vertx.transport;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.asm.Wrapper;
import com.swak.utils.Maps;
import com.swak.vertx.transport.codec.Msg;
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
public class ServiceVerticle extends AbstractVerticle implements Handler<Message<Msg>> {

	private static Logger logger = LoggerFactory.getLogger(ServiceVerticle.class);
	
	private final Object service;
	private final String address;
	private final Class<?> type;
	private final Wrapper wrapper;
	private final Map<String, MethodMeta> methods;

	public ServiceVerticle(Object service, Class<?> type) {
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
			logger.error("执行service错误", e);
		}
		Msg response = request.reset();
		
		// 错误消息
		if (error != null) {
			response.setResult(error.getMessage());
			event.reply(response);
		} 
		
		// 实现了异步接口,异步代码执行完成之后再回复
		else if (result != null && result instanceof CompletionStage) {
			CompletionStage<Object> resultFuture = (CompletionStage<Object>) result;
			resultFuture.whenComplete((r, e) -> {
				if (e == null) {
					response.setResult(r);
				} else {
					response.setError(e.getMessage());
				}
				event.reply(response);
			});
		}
		
        // 可以不用异步接口
		else if(result != null) {
			response.setResult(result);
			event.reply(response);
		} 
		
		// 回复空消息
		else {
			event.reply(response);
		}
	}
}