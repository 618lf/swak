package com.swak.vertx.transport;

import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.ClassMeta;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.asm.Wrapper;
import com.swak.vertx.transport.codec.Msg;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;

/**
 * 服务 Verticle
 *
 * @author: lifeng
 * @date: 2020/3/29 21:20
 */
public class ServiceVerticle extends AbstractVerticle implements Handler<Message<Msg>> {

	private static Logger logger = LoggerFactory.getLogger(ServiceVerticle.class);

	private final Object service;
	private final String address;
	private final Class<?> type;
	private final Wrapper wrapper;
	private final ClassMeta classMeta;

	public ServiceVerticle(Object service, Class<?> type) {
		this.service = service;
		this.type = type;
		this.address = type.getName();
		this.wrapper = Wrapper.getWrapper(type);
		this.classMeta = MethodCache.set(this.type);
	}

	/**
	 * 启动服务, startFuture.complete 底层也没有修改，暂时不知道修改方案
	 */
	@Override
	public void start(Promise<Void> startPromise) {
		this.getVertx().eventBus().<Msg>consumer(address).handler(this);
		startPromise.complete();
	}

	/**
	 * 定义停止
	 */
	@Override
	public void stop() {
		this.getVertx().eventBus().consumer(address).unregister();
	}

	/**
	 * 处理消息
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void handle(Message<Msg> event) {
		Msg request = event.body();
		MethodMeta method = this.classMeta.lookup(request.getMethodDesc());
		Object result = null;
		Throwable error = null;
		try {
			result = wrapper.invokeMethod(service, method.getMethodName(), method.getParameterTypes(),
					request.getArguments());
		} catch (Throwable e) {
			error = e.getCause() != null ? e.getCause() : e;
			logger.error("Invoke [Service:{} - Method: {}] Error.", service.getClass().getName(),
					method.getMethodDesc(), e);
		}
		Msg response = request.reset();

		// 错误消息
		if (error != null) {
			response.setError(error);
			event.reply(response);
		}

		// 实现了异步接口,异步代码执行完成之后再回复
		else if (result instanceof CompletionStage) {
			CompletionStage<Object> resultFuture = (CompletionStage<Object>) result;
			resultFuture.whenComplete((r, e) -> {
				if (e == null) {
					response.setResult(r);
				} else {
					response.setError(e);
				}
				event.reply(response);
			});
		}

		// 可以不用异步接口
		else if (result != null) {
			response.setResult(result);
			event.reply(response);
		}

		// 回复空消息
		else {
			event.reply(response);
		}
	}
}