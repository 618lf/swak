package com.swak.rabbit.logback;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import com.swak.rabbit.EventBus;
import com.swak.rabbit.message.Message;
import com.swak.rabbit.queue.QueueSenderContext;
import com.swak.utils.SpringContextHolder;
import com.swak.utils.StringUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;

/**
 * 支持通过 logback 将消息推送到 Mq 中
 * 
 * @see 优先将消息放入队列中
 * @author lifeng
 */
public class LogbackAppender extends AppenderBase<ILoggingEvent> {

	private Layout<ILoggingEvent> layout;
	private BlockingQueue<Event> events = null;
	private QueueSenderContext senderPool = null;
	private String queue;
	private String routingKey;
	private int deliveryMode = 1;
	private int maxExecutoSeconds = 30; // 最大的执行时间
	private volatile AtomicBoolean sending = new AtomicBoolean(false);

	public Layout<ILoggingEvent> getLayout() {
		return layout;
	}

	public String getQueue() {
		return queue;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public int getDeliveryMode() {
		return deliveryMode;
	}

	public int getMaxExecutoSeconds() {
		return maxExecutoSeconds;
	}

	public void setLayout(Layout<ILoggingEvent> layout) {
		this.layout = layout;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public void setDeliveryMode(int deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public void setMaxExecutoSeconds(int maxExecutoSeconds) {
		this.maxExecutoSeconds = maxExecutoSeconds;
	}

	@Override
	public void start() {
		senderPool = SpringContextHolder.getBean(QueueSenderContext.class);
		events = new LinkedBlockingDeque<>();
		super.start();
	}

	/**
	 * 添加到队列中
	 */
	@Override
	protected void append(ILoggingEvent event) {
		event.getThreadName();
		this.events.add(new Event(event));

		// 如果为false 才开启任务
		if (sending.compareAndSet(false, true)) {
			this.senderPool.execute(new EventSender());
		}
	}

	/**
	 * Helper class to actually send LoggingEvents asynchronously.
	 */
	protected class EventSender implements Runnable {

		@Override
		public void run() {
			try {
				long now = System.currentTimeMillis();
				while (true) {
					
					// 会去需要发送的数据
					final Event event = events.poll();
					if (event == null) {
						break;
					}
					
					// 发送
					this.doSend(event);

					// 超过最大的执行时间，等待下一次的執行（需要下一次的触发）
					long dur = System.currentTimeMillis();
					if ((dur - now) / 1000 > maxExecutoSeconds) {
						break;
					}
				}
			} finally {
				sending.compareAndSet(true, false);
			}
		}

		/**
		 * 这个延迟发送有问题
		 * 
		 * @param event
		 */
		private void doSend(final Event event) {
			try {
				String msgBody = layout.doLayout(event.event);
				EventBus.me().log(queue, routingKey,
						Message.of().setDeliveryMode(deliveryMode).setPayload(StringUtils.getBytesUtf8(msgBody)));
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Small helper class to encapsulate a LoggingEvent, its MDC properties, and the
	 * number of retries.
	 */
	protected static class Event {

		private final ILoggingEvent event;

		private final Map<String, String> properties;

		public Event(ILoggingEvent event) {
			this.event = event;
			this.properties = this.event.getMDCPropertyMap();
		}

		public ILoggingEvent getEvent() {
			return this.event;
		}

		public Map<String, String> getProperties() {
			return this.properties;
		}
	}
}