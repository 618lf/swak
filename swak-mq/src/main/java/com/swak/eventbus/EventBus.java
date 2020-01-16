package com.swak.eventbus;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.google.common.eventbus.AsyncEventBus;
import com.swak.reactivex.threads.SwakThreadFactory;

/**
 * 事件驱动
 * 
 * @author lifeng
 */
public class EventBus {
	private final com.google.common.eventbus.EventBus eventBus;
	private static EventBus me = null;
	private volatile boolean inited = false;

	/**
	 * 简单的事件处理
	 * 
	 * @param coreThreads
	 */
	public EventBus(Integer coreThreads) {
		eventBus = new AsyncEventBus("EventBus", Executors.newFixedThreadPool(coreThreads,
				new SwakThreadFactory("EventBus", true, new AtomicInteger())));
		me = this;
	}

	/**
	 * 初始化,返回当前是否已初始化
	 */
	public synchronized void init(Consumer<Boolean> register) {
		if (!inited) {
			this.delayConsumer(register).accept(Boolean.TRUE);
		}
		inited = true;
	}

	/**
	 * 延迟10s 注册成为消费者
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
	 * 注册成为事件消费者
	 * 
	 * @param object
	 */
	public void register(Object object) {
		eventBus.register(object);
	}

	/**
	 * 发送事件
	 * 
	 * @param event
	 */
	public void post(Object event) {
		eventBus.post(event);
	}

	/**
	 * 当前对象
	 * 
	 * @return
	 */
	public static EventBus me() {
		return me;
	}
}
