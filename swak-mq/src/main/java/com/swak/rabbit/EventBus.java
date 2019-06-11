package com.swak.rabbit;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.asm.MethodCache.MethodMeta;
import com.swak.asm.Wrapper;
import com.swak.rabbit.RabbitMQTemplate.MessageHandler;
import com.swak.rabbit.annotation.Subscribe;
import com.swak.rabbit.message.Message;
import com.swak.rabbit.message.PendingConfirm;
import com.swak.rabbit.retry.RetryStrategy;
import com.swak.utils.Lists;
import com.swak.utils.Maps;

/**
 * 基于 Mq 的事件发布实现
 * 
 * @author lifeng
 */
public class EventBus {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
	private static EventBus me = null;
	private volatile boolean inited = false;
	private RabbitMQTemplate templateForSender;
	private RabbitMQTemplate templateForConsumer;
	private RetryStrategy retryStrategy;
	private Executor executor;
	private Function<RabbitMQTemplate, Boolean> apply;

	private EventBus(RabbitMQTemplate templateForSender, RabbitMQTemplate templateForConsumer, RetryStrategy strategy,
			Executor executor, Function<RabbitMQTemplate, Boolean> apply) {
		this.templateForSender = templateForSender;
		this.templateForConsumer = templateForConsumer;
		this.retryStrategy = strategy;
		this.apply = apply;

		if (this.retryStrategy != null) {
			this.templateForSender.setConfirmCallback(this.retryStrategy);
			this.templateForSender.setReturnCallback(this.retryStrategy);
		}
	}

	/**
	 * 初始化,返回当前是否已初始化
	 */
	public synchronized void init(Consumer<Boolean> register) {
		if (!inited) {
			Optional.of(templateForConsumer).map(apply).ifPresent(register);
		}
		inited = true;
	}

	/**
	 * 注册成为事件消费者
	 * 
	 * @param object
	 */
	public void register(Object object) {
		List<Subscriber> subscribers = findAllSubscribers(object);
		for (Subscriber subscriber : subscribers) {
			templateForConsumer.basicConsume(subscriber.queue, subscriber.prefetch, subscriber);
		}
	}

	private List<Subscriber> findAllSubscribers(Object listener) {
		List<Subscriber> subscribers = Lists.newArrayList();
		Map<MethodMeta, Method> identifiers = listAnnotatedMethods(Maps.newHashMap(), listener.getClass());
		for (Method method : identifiers.values()) {
			Subscribe subscribe = method.getAnnotation(Subscribe.class);
			subscribers.add(Subscriber.create(subscribe, listener, method));
		}
		return subscribers;
	}

	private Map<MethodMeta, Method> listAnnotatedMethods(Map<MethodMeta, Method> identifiers, Class<?> clazz) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Subscribe.class) && !method.isSynthetic()) {
				MethodMeta ident = new MethodMeta(method);
				if (!identifiers.containsKey(ident)) {
					identifiers.put(ident, method);
				}
			}
		}
		Class<?> supertypes = clazz.getSuperclass();
		if (supertypes != null && !(supertypes instanceof Object)) {
			return listAnnotatedMethods(identifiers, supertypes);
		}
		return identifiers;
	}

	/**
	 * 异步 - 发送消息
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public CompletableFuture<Void> postAsync(String exchange, String routingKey, Message message) {
		if (executor == null) {
			return CompletableFuture.runAsync(() -> {
				this.post(exchange, routingKey, message);
			});
		}
		return CompletableFuture.runAsync(() -> {
			this.post(exchange, routingKey, message);
		}, executor);
	}

	/**
	 * 发送消息 log 模式， 如果需要异步发布，则可以在外部包裹发布
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public void log(String exchange, String routingKey, Message message) {
		this.templateForSender.basicPublish(exchange, routingKey, message, false);
	}

	/**
	 * 发送消息
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public void post(String exchange, String routingKey, Message message) {
		PendingConfirm pendingConfirm = null;
		try {
			pendingConfirm = this.templateForSender.basicPublish(exchange, routingKey, message);
		} catch (Exception e) {
			pendingConfirm = new PendingConfirm(message.getId());
		}
		if (this.retryStrategy != null) {
			this.bindPendingConfirm(exchange, routingKey, pendingConfirm, message);
			this.retryStrategy.add(pendingConfirm);
		}
	}

	// 将 message 绑定到 pendingConfirm 中
	private void bindPendingConfirm(String exchange, String routingKey, PendingConfirm pendingConfirm,
			Message message) {
		pendingConfirm.setDeliveryMode(message.getProperties().getDeliveryMode());
		pendingConfirm.setPriority(message.getProperties().getPriority());
		pendingConfirm.setExpiration(message.getProperties().getExpiration());
		pendingConfirm.setExchange(exchange);
		pendingConfirm.setRoutingKey(routingKey);
		pendingConfirm.setPayload(message.getPayload());
	}

	public static EventBus me() {
		return me;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private RabbitMQTemplate templateForSender;
		private RabbitMQTemplate templateForConsumer;
		private RetryStrategy strategy;
		private Executor executor;
		private Function<RabbitMQTemplate, Boolean> apply;

		public Builder setTemplateForSender(RabbitMQTemplate templateForSender) {
			this.templateForSender = templateForSender;
			return this;
		}

		public Builder setTemplateForConsumer(RabbitMQTemplate templateForConsumer) {
			this.templateForConsumer = templateForConsumer;
			return this;
		}

		public Builder setStrategy(RetryStrategy strategy) {
			this.strategy = strategy;
			return this;
		}

		public Builder setExecutor(Executor executor) {
			this.executor = executor;
			return this;
		}

		public Builder setApply(Function<RabbitMQTemplate, Boolean> apply) {
			this.apply = apply;
			return this;
		}

		public EventBus build() {
			EventBus eventBus = new EventBus(templateForSender,
					templateForConsumer == null ? templateForSender : templateForConsumer, strategy, executor, apply);
			me = eventBus;
			return eventBus;
		}
	}

	/**
	 * 订阅服务
	 * 
	 * @author lifeng
	 */
	public static class Subscriber implements MessageHandler {
		private static Class<?>[] types = new Class<?>[] { Message.class };
		private String queue;
		private int prefetch;
		private Object listener;
		private Method method;

		public static Subscriber create(Subscribe subscribe, Object listener, Method method) {
			Subscriber subscriber = new Subscriber();
			subscriber.queue = subscribe.queue();
			subscriber.prefetch = subscribe.prefetch();
			subscriber.listener = listener;
			subscriber.method = method;
			return subscriber;
		}

		/**
		 * 消费消息
		 */
		@Override
		public Object handle(Message message) throws AmqpException {
			try {
				
				// 处理器
				Wrapper wrapper = Wrapper.getWrapper(listener.getClass());
				Object[] args = new Object[] { message };

				// 执行处理器
				return wrapper.invokeMethod(listener, method.getName(), types, args);
			} catch (Exception e) {
				LOGGER.error("Handler{} - Method{} Invoke Error：", listener.getClass(), method.getName(), e);
				throw new AmqpException("处理消费事件错误：", e);
			}
		}
	}
}