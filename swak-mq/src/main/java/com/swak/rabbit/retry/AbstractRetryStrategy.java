package com.swak.rabbit.retry;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import com.swak.rabbit.RabbitMQTemplate;
import com.swak.rabbit.message.Message;
import com.swak.rabbit.message.PendingConfirm;
import com.swak.reactivex.transport.resources.EventLoopFactory;
import com.swak.reactivex.transport.resources.EventLoops;
import com.swak.utils.Ints;
import com.swak.utils.JsonMapper;

/**
 * 待重试功能
 * 
 * @author lifeng
 */
public abstract class AbstractRetryStrategy implements RetryStrategy, Runnable, DisposableBean {

	private Logger logger = LoggerFactory.getLogger(RetryStrategy.class);
	private RabbitMQTemplate template;
	private ScheduledExecutorService executor;
	private int threadPool = 1;
	private int minutes = 1;
	private int awaitTime = 30; // 30s

	public AbstractRetryStrategy() {
		start();
	}

	public AbstractRetryStrategy(int threadPool, int minutes) {
		this.threadPool = threadPool;
		this.minutes = minutes;
		start();
	}

	public void bindSender(RabbitMQTemplate template) {
		this.template = template;
	}

	@Override
	public RabbitMQTemplate getSender() {
		return template;
	}

	/**
	 * 启动服务
	 */
	protected void start() {
		ThreadFactory threadFactory = new EventLoopFactory(false, "Retry.", new AtomicLong());
		executor = Executors.newScheduledThreadPool(threadPool, threadFactory);
		executor.scheduleAtFixedRate(this, 3, minutes, TimeUnit.MINUTES);
		EventLoops.register("Retry", executor);
	}

	/**
	 * 开始判断是否需要重发
	 */
	@Override
	public void run() {
		// 直到获取一个元素
		try {
			while (true) {

				// 检查队列是否有元素
				PendingConfirm pendingConfirm = this.headGet();
				if (pendingConfirm == null) {
					break;
				}

				// 已经确定为已应答的数据
				if (hasAcked(pendingConfirm)) {
					headRemove();
					continue;
				}

				// 最多三次重试
				if (pendingConfirm.getRetryTimes() > 3) {
					headRemove();
					LOGGER.error("Send message {} failed after 3 times ", JsonMapper.toJson(pendingConfirm));
					continue;
				}

				// 超过1分钟的则需要重试
				if (pendingConfirm.getTimestamp() + 1 * 60 * 1000 < System.currentTimeMillis()) {
					headRemove();
					this.retrySender(pendingConfirm);
					continue;
				}

				// 队列首不是这样的数据，则后面的数据都不需要处理
				break;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 重试发送, 并放入重試队列
	 * 
	 * @param pendingConfirm
	 */
	protected void retrySender(PendingConfirm pendingConfirm) {
		try {
			this.getSender().basicPublish(pendingConfirm.getExchange(), pendingConfirm.getRoutingKey(),
					Message.builder().setId(pendingConfirm.getId()).setDeliveryMode(pendingConfirm.getDeliveryMode())
							.setExpiration(pendingConfirm.getExpiration()).setPriority(pendingConfirm.getPriority())
							.setPayload(pendingConfirm.getPayload()).build());
		} catch (Exception e) {
			logger.error("Retry Send Message Error:", e);
		}
		pendingConfirm.setRetryTimes(Ints.add(pendingConfirm.getRetryTimes(), 1));
		this.add(pendingConfirm);
	}

	/**
	 * 关闭服务
	 */
	@Override
	public void destroy() throws Exception {
		try {
			executor.shutdown();
			if (!executor.awaitTermination(awaitTime, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
		}
	}

	/**
	 * 获取头部数据，只获取，不删除
	 * 
	 * @return
	 */
	protected abstract PendingConfirm headGet();

	/**
	 * 是否已经应答
	 * 
	 * @param pendingConfirm
	 * @return
	 */
	protected abstract boolean hasAcked(PendingConfirm pendingConfirm);

	/**
	 * 获取头部数据，只获取，不删除
	 * 
	 * @return
	 */
	protected abstract void headRemove();
}