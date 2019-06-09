package com.swak.rocketmq;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

import com.swak.asm.MethodCache.MethodMeta;
import com.swak.asm.Wrapper;
import com.swak.rocketmq.annotation.ConsumeMode;
import com.swak.rocketmq.annotation.Listener;
import com.swak.rocketmq.annotation.MessageModel;
import com.swak.rocketmq.annotation.SelectorType;
import com.swak.rocketmq.annotation.Subscribe;
import com.swak.rocketmq.message.Message;
import com.swak.rocketmq.transaction.RocketMQLocalTransactionListener;
import com.swak.utils.Lists;
import com.swak.utils.Maps;

public class EventBus {

	private final static Logger log = LoggerFactory.getLogger(EventBus.class);
	private List<Subscriber> subscribers = Lists.newArrayList();
	private List<TransactionHandler> handlers = Lists.newArrayList();
	private RocketMQTemplate rocketMQTemplate;

	/**
	 * 注册成为事件消费者
	 * 
	 * @param object
	 */
	public void register(Object object) {
		List<Subscriber> subscribers = findAllSubscribers(object);
		for (Subscriber subscriber : subscribers) {
			if (!subscriber.isRunning()) {
				subscriber.start();
			}
		}
	}

	private List<Subscriber> findAllSubscribers(Object listener) {
		Map<MethodMeta, Method> identifiers = listAnnotatedMethods(Maps.newHashMap(), listener.getClass(),
				Subscribe.class);
		for (Method method : identifiers.values()) {
			Subscribe subscribe = method.getAnnotation(Subscribe.class);
			subscribers.add(Subscriber.create(subscribe, listener, method));
		}
		return subscribers;
	}

	/**
	 * 生产者事务监听
	 * 
	 * @param object
	 * @throws MQClientException 
	 */
	public void listener(Object object) throws MQClientException {
		List<TransactionHandler> subscribers = findAllTransactionHandlers(object);
		for (TransactionHandler subscriber : subscribers) {
			rocketMQTemplate.registerTransactionHandler(subscriber);
		}
	}

	private List<TransactionHandler> findAllTransactionHandlers(Object listener) {
		Map<MethodMeta, Method> identifiers = listAnnotatedMethods(Maps.newHashMap(), listener.getClass(),
				Listener.class);
		for (Method method : identifiers.values()) {
			Listener subscribe = method.getAnnotation(Listener.class);
			handlers.add(TransactionHandler.create(subscribe, listener, method));
		}
		return handlers;
	}

	private Map<MethodMeta, Method> listAnnotatedMethods(Map<MethodMeta, Method> identifiers, Class<?> clazz,
			Class<? extends Annotation> annotation) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(annotation) && !method.isSynthetic()) {
				MethodMeta ident = new MethodMeta(method);
				if (!identifiers.containsKey(ident)) {
					identifiers.put(ident, method);
				}
			}
		}
		Class<?> supertypes = clazz.getSuperclass();
		if (supertypes != null && !(supertypes instanceof Object)) {
			return listAnnotatedMethods(identifiers, supertypes, annotation);
		}
		return identifiers;
	}

	/**
	 * 消费处理
	 * 
	 * @author lifeng
	 */
	public static class Subscriber {
		private static Class<?>[] types = new Class<?>[] { Message.class };
		private long suspendCurrentQueueTimeMillis = 1000;
		private int delayLevelWhenNextConsume = 0;
		private String nameServer;
		private String consumerGroup;
		private String topic;
		private int consumeThreadMax = 64;
		private DefaultMQPushConsumer consumer;

		private boolean running;

		// The following properties came from @RocketMQMessageListener.
		private ConsumeMode consumeMode;
		private SelectorType selectorType;
		private String selectorExpression;
		private MessageModel messageModel;

		// 具体的监听器
		private Object listener;
		private Method method;

		public Subscriber(Subscribe subscribe) {
			this.consumeMode = subscribe.consumeMode();
			this.consumeThreadMax = subscribe.consumeThreadMax();
			this.messageModel = subscribe.messageModel();
			this.selectorExpression = subscribe.selectorExpression();
			this.selectorType = subscribe.selectorType();
		}

		public boolean isRunning() {
			return running;
		}

		private void setRunning(boolean running) {
			this.running = running;
		}

		/**
		 * 停止
		 */
		public void stop() {
			if (this.isRunning()) {
				if (Objects.nonNull(consumer)) {
					consumer.shutdown();
				}
				setRunning(false);
			}
		}

		/**
		 * 启动
		 */
		public void start() {
			if (this.isRunning()) {
				throw new IllegalStateException("container already running. " + this.toString());
			}
			try {
				initRocketMQPushConsumer();
				consumer.start();
			} catch (MQClientException e) {
				throw new IllegalStateException("Failed to start RocketMQ push consumer", e);
			}
			this.setRunning(true);

			log.info("running container: {}", this.toString());
		}

		private void initRocketMQPushConsumer() throws MQClientException {
			Assert.notNull(listener, "Property 'listener' is required");
			Assert.notNull(consumerGroup, "Property 'consumerGroup' is required");
			Assert.notNull(nameServer, "Property 'nameServer' is required");
			Assert.notNull(topic, "Property 'topic' is required");

			consumer = new DefaultMQPushConsumer(consumerGroup);
			consumer.setNamesrvAddr(nameServer);
			consumer.setConsumeThreadMax(consumeThreadMax);
			if (consumeThreadMax < consumer.getConsumeThreadMin()) {
				consumer.setConsumeThreadMin(consumeThreadMax);
			}

			switch (messageModel) {
			case BROADCASTING:
				consumer.setMessageModel(org.apache.rocketmq.common.protocol.heartbeat.MessageModel.BROADCASTING);
				break;
			case CLUSTERING:
				consumer.setMessageModel(org.apache.rocketmq.common.protocol.heartbeat.MessageModel.CLUSTERING);
				break;
			default:
				throw new IllegalArgumentException("Property 'messageModel' was wrong.");
			}

			switch (selectorType) {
			case TAG:
				consumer.subscribe(topic, selectorExpression);
				break;
			case SQL92:
				consumer.subscribe(topic, MessageSelector.bySql(selectorExpression));
				break;
			default:
				throw new IllegalArgumentException("Property 'selectorType' was wrong.");
			}

			switch (consumeMode) {
			case ORDERLY:
				consumer.setMessageListener(new DefaultMessageListenerOrderly());
				break;
			case CONCURRENTLY:
				consumer.setMessageListener(new DefaultMessageListenerConcurrently());
				break;
			default:
				throw new IllegalArgumentException("Property 'consumeMode' was wrong.");
			}
		}

		public class DefaultMessageListenerConcurrently implements MessageListenerConcurrently {

			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				for (MessageExt messageExt : msgs) {
					log.debug("received msg: {}", messageExt);
					try {
						long now = System.currentTimeMillis();
						handleMessage(doConvertMessage(messageExt));
						long costTime = System.currentTimeMillis() - now;
						log.debug("consume {} cost: {} ms", messageExt.getMsgId(), costTime);
					} catch (Exception e) {
						log.warn("consume message failed. messageExt:{}", messageExt, e);
						context.setDelayLevelWhenNextConsume(delayLevelWhenNextConsume);
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
				}

				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		}

		public class DefaultMessageListenerOrderly implements MessageListenerOrderly {

			@Override
			public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
				for (MessageExt messageExt : msgs) {
					log.debug("received msg: {}", messageExt);
					try {
						long now = System.currentTimeMillis();
						handleMessage(doConvertMessage(messageExt));
						long costTime = System.currentTimeMillis() - now;
						log.info("consume {} cost: {} ms", messageExt.getMsgId(), costTime);
					} catch (Exception e) {
						log.warn("consume message failed. messageExt:{}", messageExt, e);
						context.setSuspendCurrentQueueTimeMillis(suspendCurrentQueueTimeMillis);
						return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
					}
				}

				return ConsumeOrderlyStatus.SUCCESS;
			}
		}

		/**
		 * 消费消息
		 */
		private void handleMessage(Message message) {
			Wrapper wrapper = Wrapper.getWrapper(listener.getClass());
			Object[] args = new Object[] { message };
			try {
				wrapper.invokeMethod(listener, method.getName(), types, args);
			} catch (Exception e) {
				log.error("handle message error: {}", e);
			}
		}

		/**
		 * 消息转换
		 * 
		 * @param messageExt
		 * @return
		 */
		private Message doConvertMessage(MessageExt messageExt) {
			return null;
		}

		/**
		 * 创建一个处理器
		 * 
		 * @param subscribe
		 * @param listener
		 * @param method
		 * @return
		 */
		public static Subscriber create(Subscribe subscribe, Object listener, Method method) {
			Subscriber subscriber = new Subscriber(subscribe);
			subscriber.listener = listener;
			subscriber.method = method;
			return subscriber;
		}
	}

	/**
	 * 事务处理器
	 * 
	 * @author lifeng
	 */
	public static class TransactionHandler {

		private String name;
		private String beanName;
		private RocketMQLocalTransactionListener bean;
		private BeanFactory beanFactory;
		private ThreadPoolExecutor checkExecutor;

		public String getBeanName() {
			return beanName;
		}

		public void setBeanName(String beanName) {
			this.beanName = beanName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public BeanFactory getBeanFactory() {
			return beanFactory;
		}

		public void setBeanFactory(BeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}

		public void setListener(RocketMQLocalTransactionListener listener) {
			this.bean = listener;
		}

		public RocketMQLocalTransactionListener getListener() {
			return this.bean;
		}

		public void setCheckExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, int blockingQueueSize) {
			this.checkExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
					new LinkedBlockingDeque<>(blockingQueueSize));
		}

		public ThreadPoolExecutor getCheckExecutor() {
			return checkExecutor;
		}

		/**
		 * 创建一个处理器
		 * 
		 * @param subscribe
		 * @param listener
		 * @param method
		 * @return
		 */
		public static TransactionHandler create(Listener subscribe, Object listener, Method method) {
			TransactionHandler subscriber = new TransactionHandler();
			return subscriber;
		}
	}
}
