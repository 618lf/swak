package com.swak.rabbit.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;

import org.springframework.util.Assert;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.rabbit.EventBus;
import com.swak.rabbit.annotation.Publisher;
import com.swak.rabbit.message.Message;
import com.swak.utils.StringUtils;

/**
 * 消息发送处理
 * 
 * @author lifeng
 */
public class InvokerHandler implements InvocationHandler {

	private final Class<?> type;
	private final String queue;
	private final String routing;

	public InvokerHandler(Publisher publisher, Class<?> type) {
		this.queue = publisher.queue();
		this.routing = StringUtils.isBlank(publisher.routing()) ? this.queue : publisher.routing();
		this.type = type;
		this.initMethods();
	}

	private void initMethods() {
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			MethodCache.set(method);
		}
	}

	/**
	 * 发送消息
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Assert.notNull(args, "args can not null");
		Assert.notEmpty(args, "args can not empty");
		String queue = this.queue;
		String routing = this.routing;
		Object message = null;
		if (args.length == 1) {
			Assert.notNull(args[0], "args[0] can not null, args[0] is message[String, Object, Message].");
			message = args[0];
		} else if (args.length == 2) {
			Assert.notNull(args[0], "args[0] can not null, args[0] is queue[String].");
			Assert.notNull(args[1], "args[1] can not null, args[1] is message[String, Object, Message].");
			queue = String.valueOf(args[0]);
			routing = queue;
			message = args[1];
		} else if (args.length >= 3) {
			Assert.notNull(args[0], "args[0] can not null, args[0] is queue[String].");
			Assert.notNull(args[1], "args[1] can not null, args[1] is routing[String].");
			Assert.notNull(args[1], "args[2] can not null, args[2] is message[String, Object, Message].");
			queue = String.valueOf(args[0]);
			routing = String.valueOf(args[1]);
			message = args[2];
		}
		// 返回类型决定是同步执行还是异步执行
		MethodMeta meta = MethodCache.get(method);
		Class<?> returnType = meta.getReturnType();

		// 异步执行
		if (returnType.isAssignableFrom(CompletionStage.class)) {
			return this.postAsync(queue, routing, message);
		}
		return this.post(queue, routing, message);
	}

	private Object post(String queue, String routingKey, Object message) {
		if (message instanceof String) {
			EventBus.me().post(queue, queue, (String) message);
		} else if (message instanceof Message) {
			EventBus.me().post(queue, queue, (Message) message);
		} else {
			EventBus.me().post(queue, queue, message);
		}
		return null;
	}

	private Object postAsync(String queue, String routingKey, Object message) {
		if (message instanceof String) {
			return EventBus.me().submit(queue, queue, (String) message);
		} else if (message instanceof Message) {
			return EventBus.me().submit(queue, queue, (Message) message);
		} else {
			return EventBus.me().submit(queue, queue, message);
		}
	}
}