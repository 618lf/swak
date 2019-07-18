package com.swak.rabbit.logback;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.swak.rabbit.EventBus;
import com.swak.rabbit.message.Message;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.WorkerContext;
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
	private String queue;
	private String routingKey;
	private int deliveryMode = 1;
	private int maxExecutoSeconds = 30; // 最大的执行时间
	private boolean useAloneExecutor = false;
	private WorkerContext context;
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

	public void setUseAloneExecutor(boolean useAloneExecutor) {
		this.useAloneExecutor = useAloneExecutor;
	}

	@Override
	public void start() {
		events = new LinkedBlockingDeque<>();
		if (useAloneExecutor) {
			context = Contexts.createWorkerContext("Rabbit.logger-", 1, true, 60, TimeUnit.SECONDS);
		}
		super.start();
	}

	/**
	 * 添加到队列中
	 */
	@Override
	protected void append(ILoggingEvent event) {
		event.getThreadName();
		this.events.add(new Event(event));
		this.prepareTask();
	}

	/**
	 * 添加一次执行任务
	 */
	private void prepareTask() {
		if (sending.compareAndSet(false, true)) {
			if (context != null) {
				context.execute(new EventSender());
			} else {
				EventBus.me().execute(new EventSender());
			}
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
				this.prepareNextTask();
			}
		}

		/**
		 * 特殊情况下使用阻塞式的发送
		 * 
		 * @param event
		 */
		@SuppressWarnings("deprecation")
		private void doSend(final Event event) {
			try {
				String msgBody = layout.doLayout(event.event);
				EventBus.me().blockSend(queue, routingKey,
						Message.of().setDeliveryMode(deliveryMode).setPayload(StringUtils.getBytesUtf8(msgBody)),
						false);
			} catch (Exception e) {
			}
		}

		/**
		 * 准备下一次的执行
		 */
		private void prepareNextTask() {
			if (sending.compareAndSet(true, false) && events.peek() != null) {
				prepareTask();
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