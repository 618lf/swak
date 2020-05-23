package com.swak.rabbit;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.asm.Wrapper;
import com.swak.rabbit.RabbitMQTemplate.MessageHandler;
import com.swak.rabbit.annotation.Subscribe;
import com.swak.rabbit.message.Message;
import com.swak.rabbit.message.PendingConfirm;
import com.swak.rabbit.retry.RetryStrategy;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

/**
 * 基于 Mq 的事件发布实现
 * 
 * @author lifeng
 */
public class EventBus {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
	private static EventBus me = null;
	private volatile boolean inited = false;
	private volatile AtomicBoolean sending = new AtomicBoolean(false);
	private RabbitMQTemplate templateForSender;
	private RabbitMQTemplate templateForConsumer;
	private RetryStrategy retryStrategy;
	private Executor executor;
	private Function<RabbitMQTemplate, Boolean> apply;
	private BlockingQueue<Message> queue = null;

	private EventBus(RabbitMQTemplate templateForSender, RabbitMQTemplate templateForConsumer, RetryStrategy strategy,
			Executor executor, Function<RabbitMQTemplate, Boolean> apply) {
		this.templateForSender = templateForSender;
		this.templateForConsumer = templateForConsumer;
		this.executor = executor == null ? ForkJoinPool.commonPool() : executor;
		this.retryStrategy = strategy;
		this.queue = new LinkedBlockingDeque<>();
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
			Optional.of(templateForConsumer).map(t -> this.apply(t)).map(apply).ifPresent(this.delayConsumer(register));
		}
		inited = true;
	}

	/**
	 * 注册成为消费者
	 * 
	 * @param register
	 * @return
	 */
	private Consumer<Boolean> delayConsumer(Consumer<Boolean> register) {
		return (t) -> {
			register.accept(t);
		};
	}

	/**
	 * 默认的队列
	 * 
	 * @param template
	 * @return
	 */
	private RabbitMQTemplate apply(RabbitMQTemplate sender) {

		// 定义延迟队列
		Map<String, Object> agruments = Maps.newHashMap();
		agruments.put("x-dead-letter-exchange", Constants.retry_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry_channel);
		agruments.put("x-message-ttl", Constants.dead);
		sender.exchangeDirectBindQueue(Constants.dead_channel, Constants.dead_channel, Constants.dead_channel,
				agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry1s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry1s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[0]);
		sender.exchangeDirectBindQueue(Constants.retry1s_channel, Constants.retry1s_channel, Constants.retry1s_channel,
				agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry5s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry5s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[1]);
		sender.exchangeDirectBindQueue(Constants.retry5s_channel, Constants.retry5s_channel, Constants.retry5s_channel,
				agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry10s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry10s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[2]);
		sender.exchangeDirectBindQueue(Constants.retry10s_channel, Constants.retry10s_channel,
				Constants.retry10s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry30s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry30s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[3]);
		sender.exchangeDirectBindQueue(Constants.retry30s_channel, Constants.retry30s_channel,
				Constants.retry30s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry60s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry60s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[4]);
		sender.exchangeDirectBindQueue(Constants.retry60s_channel, Constants.retry60s_channel,
				Constants.retry60s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry120s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry120s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[5]);
		sender.exchangeDirectBindQueue(Constants.retry120s_channel, Constants.retry120s_channel,
				Constants.retry120s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry180s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry180s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[6]);
		sender.exchangeDirectBindQueue(Constants.retry180s_channel, Constants.retry180s_channel,
				Constants.retry180s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry240s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry240s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[7]);
		sender.exchangeDirectBindQueue(Constants.retry240s_channel, Constants.retry240s_channel,
				Constants.retry240s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry300s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry300s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[8]);
		sender.exchangeDirectBindQueue(Constants.retry300s_channel, Constants.retry300s_channel,
				Constants.retry300s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry360s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry360s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[9]);
		sender.exchangeDirectBindQueue(Constants.retry360s_channel, Constants.retry360s_channel,
				Constants.retry360s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry420s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry420s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[10]);
		sender.exchangeDirectBindQueue(Constants.retry420s_channel, Constants.retry420s_channel,
				Constants.retry420s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry480s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry480s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[11]);
		sender.exchangeDirectBindQueue(Constants.retry480s_channel, Constants.retry480s_channel,
				Constants.retry480s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry540s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry540s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[12]);
		sender.exchangeDirectBindQueue(Constants.retry540s_channel, Constants.retry540s_channel,
				Constants.retry540s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry600s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry600s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[13]);
		sender.exchangeDirectBindQueue(Constants.retry600s_channel, Constants.retry600s_channel,
				Constants.retry600s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry1200s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry1200s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[14]);
		sender.exchangeDirectBindQueue(Constants.retry1200s_channel, Constants.retry1200s_channel,
				Constants.retry1200s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry1800s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry1800s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[15]);
		sender.exchangeDirectBindQueue(Constants.retry1800s_channel, Constants.retry1800s_channel,
				Constants.retry1800s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry3600s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry3600s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[16]);
		sender.exchangeDirectBindQueue(Constants.retry3600s_channel, Constants.retry3600s_channel,
				Constants.retry3600s_channel, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry7200s_channel_cus);
		agruments.put("x-dead-letter-routing-key", Constants.retry7200s_channel_cus);
		agruments.put("x-message-ttl", Constants.retrys[17]);
		sender.exchangeDirectBindQueue(Constants.retry7200s_channel, Constants.retry7200s_channel,
				Constants.retry7200s_channel, agruments);

		// 定义延迟队列 的消费在队列
		return this.applyCus(sender);
	}

	/**
	 * 默认的队列
	 * 
	 * @param template
	 * @return
	 */
	private RabbitMQTemplate applyCus(RabbitMQTemplate sender) {

		Map<String, Object> agruments = Maps.newHashMap();
		agruments.put("x-dead-letter-exchange", Constants.retry1s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry1s_channel);
		sender.exchangeDirectBindQueue(Constants.retry_channel, Constants.retry_channel, Constants.retry_channel,
				agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry5s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry5s_channel);
		sender.exchangeDirectBindQueue(Constants.retry1s_channel_cus, Constants.retry1s_channel_cus,
				Constants.retry1s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry10s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry10s_channel);
		sender.exchangeDirectBindQueue(Constants.retry5s_channel_cus, Constants.retry5s_channel_cus,
				Constants.retry5s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry30s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry30s_channel);
		sender.exchangeDirectBindQueue(Constants.retry10s_channel_cus, Constants.retry10s_channel_cus,
				Constants.retry10s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry60s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry60s_channel);
		sender.exchangeDirectBindQueue(Constants.retry30s_channel_cus, Constants.retry30s_channel_cus,
				Constants.retry30s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry120s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry120s_channel);
		sender.exchangeDirectBindQueue(Constants.retry60s_channel_cus, Constants.retry60s_channel_cus,
				Constants.retry60s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry180s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry180s_channel);
		sender.exchangeDirectBindQueue(Constants.retry120s_channel_cus, Constants.retry120s_channel_cus,
				Constants.retry120s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry240s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry240s_channel);
		sender.exchangeDirectBindQueue(Constants.retry180s_channel_cus, Constants.retry180s_channel_cus,
				Constants.retry180s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry300s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry300s_channel);
		sender.exchangeDirectBindQueue(Constants.retry240s_channel_cus, Constants.retry240s_channel_cus,
				Constants.retry240s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry360s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry360s_channel);
		sender.exchangeDirectBindQueue(Constants.retry300s_channel_cus, Constants.retry300s_channel_cus,
				Constants.retry300s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry420s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry420s_channel);
		sender.exchangeDirectBindQueue(Constants.retry360s_channel_cus, Constants.retry360s_channel_cus,
				Constants.retry360s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry480s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry480s_channel);
		sender.exchangeDirectBindQueue(Constants.retry420s_channel_cus, Constants.retry420s_channel_cus,
				Constants.retry420s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry540s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry540s_channel);
		sender.exchangeDirectBindQueue(Constants.retry480s_channel_cus, Constants.retry480s_channel_cus,
				Constants.retry480s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry600s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry600s_channel);
		sender.exchangeDirectBindQueue(Constants.retry540s_channel_cus, Constants.retry540s_channel_cus,
				Constants.retry540s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry1200s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry1200s_channel);
		sender.exchangeDirectBindQueue(Constants.retry600s_channel_cus, Constants.retry600s_channel_cus,
				Constants.retry600s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry1800s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry1800s_channel);
		sender.exchangeDirectBindQueue(Constants.retry1200s_channel_cus, Constants.retry1200s_channel_cus,
				Constants.retry1200s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry3600s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry3600s_channel);
		sender.exchangeDirectBindQueue(Constants.retry1800s_channel_cus, Constants.retry1800s_channel_cus,
				Constants.retry1800s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.retry7200s_channel);
		agruments.put("x-dead-letter-routing-key", Constants.retry7200s_channel);
		sender.exchangeDirectBindQueue(Constants.retry3600s_channel_cus, Constants.retry3600s_channel_cus,
				Constants.retry3600s_channel_cus, agruments);

		agruments.put("x-dead-letter-exchange", Constants.fail_channel);
		agruments.put("x-dead-letter-routing-key", Constants.fail_channel);
		sender.exchangeDirectBindQueue(Constants.retry7200s_channel_cus, Constants.retry7200s_channel_cus,
				Constants.retry7200s_channel_cus, agruments);

		sender.exchangeDirectBindQueue(Constants.fail_channel, Constants.fail_channel, Constants.fail_channel, null);

		return sender;
	}

	/**
	 * 注册成为事件消费者
	 * 
	 * @param object
	 */
	public void register(Object object) {
		List<Subscriber> subscribers = findAllSubscribers(object);
		subscribers.stream().flatMap(subscriber -> {
			return Stream.iterate(0, t -> t + 1).limit(subscriber.parallel).map(t -> subscriber);
		}).forEach(subscriber -> {
			templateForConsumer.basicConsume(subscriber.queue, subscriber.prefetch, subscriber);
		});
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
		for (Method method : clazz.getMethods()) {
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
	 * 应用消费者
	 * 
	 * @Title: applyConsumer
	 * @param apply
	 * @author lifeng
	 * @date 2019-11-23 12:49:19
	 */
	public void applyConsumer(Consumer<RabbitMQTemplate> apply) {
		apply.accept(this.templateForConsumer);
	}

	/**
	 * 应用发送者
	 * 
	 * @Title: applySender
	 * @param apply
	 * @author lifeng
	 * @date 2019-11-23 12:49:29
	 */
	public void applySender(Consumer<RabbitMQTemplate> apply) {
		apply.accept(this.templateForSender);
	}

	// 异步发送，提交到任务队列中
	/**
	 * 提交任务
	 * 
	 * @param runnable
	 */
	public void execute(Runnable runnable) {
		this.executor.execute(runnable);
	}

	// 异步发送，消息先提交到队列，通过线程池来小任务的提交到队列
	/**
	 * 队列发送
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public void queue(String exchange, String routingKey, Message message) {
		Assert.notNull(message, "Message can not null!");
		this.queue.add(message.setExchange(exchange).setRoutingKey(routingKey));
		this.prepareTask();
	}

	/**
	 * 添加一次执行任务
	 */
	private void prepareTask() {
		if (sending.compareAndSet(false, true)) {
			this.executor.execute(new QueueRunable());
		}
	}

	// 同步发送消息，保证消息提交到队列

	/**
	 * 发送消息
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public void post(String exchange, String routingKey, Object message) {
		Assert.notNull(message, "Message can not null!");
		Message _message = Message.of().object2Payload(message);
		this.post(exchange, routingKey, _message);
	}

	/**
	 * 发送消息 -- String, 获取消息时需要自己转换
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public void post(String exchange, String routingKey, String message) {
		Assert.notNull(message, "Message can not null!");
		Message _message = Message.of().setPayload(StringUtils.getBytesUtf8(message));
		this.post(exchange, routingKey, _message);
	}

	/**
	 * 异步的发送消息，将消息提交到任务隊列等待發送
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public void post(String exchange, String routingKey, Message message) {
		this.blockSend(exchange, routingKey, message, true);
	}

	// 将消息提交到队列，需要等待消息队列的返回才能继续执行

	/**
	 * 发送消息
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public CompletableFuture<Void> submit(String exchange, String routingKey, Object message) {
		Assert.notNull(message, "Message can not null!");
		Message _message = Message.of().object2Payload(message);
		return this.submit(exchange, routingKey, _message);
	}

	/**
	 * 发送消息 -- String, 获取消息时需要自己转换
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public CompletableFuture<Void> submit(String exchange, String routingKey, String message) {
		Assert.notNull(message, "Message can not null!");
		Message _message = Message.of().setPayload(StringUtils.getBytesUtf8(message));
		return this.submit(exchange, routingKey, _message);
	}

	/**
	 * 异步 - 发送消息
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public CompletableFuture<Void> submit(String exchange, String routingKey, Message message) {
		return CompletableFuture.runAsync(() -> {
			this.blockSend(exchange, routingKey, message, true);
		}, executor);
	}

	/**
	 * 阻塞式的消息发送方式（会阻塞调用线程）
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param message
	 * @param retryable
	 */
	@Deprecated
	public void blockSend(String exchange, String routingKey, Message message, boolean retryable) {
		message = message.build();
		PendingConfirm pendingConfirm = null;
		try {
			pendingConfirm = this.templateForSender.basicPublish(exchange, routingKey, message, retryable);
		} catch (Exception e) {
			pendingConfirm = new PendingConfirm(message.getId());
		}
		if (this.retryStrategy != null && retryable) {
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

	/**
	 * 当前对象
	 * 
	 * @return
	 */
	public static EventBus me() {
		return me;
	}

	/**
	 * 队列发送
	 * 
	 * @author lifeng
	 */
	public class QueueRunable implements Runnable {

		@Override
		public void run() {
			try {
				long now = System.currentTimeMillis();
				while (true) {
					final Message event = queue.poll();
					if (event == null) {
						break;
					}
					this.doSend(event);
					long dur = System.currentTimeMillis();
					if (dur - now >= 1000) {
						break;
					}
				}
			} finally {
				this.prepareNextTask();
			}
		}

		/**
		 * 特殊情况下使用阻塞式的发送
		 * 
		 * @param event
		 */
		private void doSend(final Message event) {
			try {
				EventBus.me().blockSend(event.getExchange(), event.getRoutingKey(), event, true);
			} catch (Exception e) {
			}
		}

		/**
		 * 准备下一次的执行
		 */
		private void prepareNextTask() {
			if (sending.compareAndSet(true, false) && queue.peek() != null) {
				prepareTask();
			}
		}
	}

	/**
	 * 构造对象
	 * 
	 * @return
	 */
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
	 * 定义通用的消息发送接口
	 * 
	 * @ClassName: MessagePublisher
	 * @author: lifeng
	 * @date: Nov 22, 2019 10:44:33 AM
	 */
	public static interface MessagePublisher {

		/**
		 * 发送消息
		 * 
		 * @Title: post
		 * @param event
		 * @author lifeng
		 * @date 2019-11-22 10:45:36
		 */
		<T> void post(T event);

		/**
		 * 发送消息
		 * 
		 * @Title: post
		 * @param event
		 * @author lifeng
		 * @date 2019-11-22 10:45:36
		 */
		<T> void post(String queue, T event);

		/**
		 * 发送消息
		 * 
		 * @Title: submit
		 * @param event
		 * @author lifeng
		 * @date 2019-11-22 10:45:36
		 */
		<T> CompletableFuture<Void> submit(T event);

		/**
		 * 发送消息
		 * 
		 * @Title: submit
		 * @param event
		 * @author lifeng
		 * @date 2019-11-22 10:45:36
		 */
		<T> CompletableFuture<Void> submit(String queue, T event);
	}

	/**
	 * 订阅服务
	 * 
	 * @author lifeng
	 */
	public static class Subscriber implements MessageHandler {
		private String queue;
		private int prefetch;
		private int parallel;
		private Object listener;

		// 处理器
		private Wrapper wrapper;
		private MethodMeta methodMeta;

		public static Subscriber create(Subscribe subscribe, Object listener, Method method) {
			Subscriber subscriber = new Subscriber();
			subscriber.queue = subscribe.queue();
			subscriber.prefetch = subscribe.prefetch();
			subscriber.parallel = subscribe.parallel();
			subscriber.listener = listener;
			subscriber.wrapper = Wrapper.getWrapper(listener.getClass());
			subscriber.methodMeta = MethodCache.get(listener.getClass()).lookup(method);
			return subscriber;
		}

		/**
		 * 消费消息
		 */
		@Override
		public Object handle(Message message) throws AmqpException {
			try {
				return wrapper.invokeMethod(listener, methodMeta.getMethodDesc(), new Object[] { message });
			} catch (Exception e) {
				LOGGER.error("Handler[{}] - Method [{}] Invoke Error：", listener.getClass().getName(),
						methodMeta.getMethodDesc(), e.getCause());
				throw new AmqpException("处理消费事件错误");
			}
		}
	}
}