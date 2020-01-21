package com.swak.rxtx.channel;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.reactivex.threads.Context;
import com.swak.reactivex.threads.SwakThreadFactory;

import io.netty.util.internal.ObjectUtil;

/**
 * 线程池
 * 
 * @author lifeng
 */
public class EventLoop extends ThreadPoolExecutor implements Context {

	private EventLoopGroup parent;
	private volatile Thread thread;

	/**
	 * 定义一个线程的线程池
	 */
	public EventLoop() {
		super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
				new SwakThreadFactory("Channels.EventLoop-", true, new AtomicInteger(0)));
	}

	/**
	 * 记录唯一线程
	 */
	@Override
	public void execute(Runnable task) {
		ObjectUtil.checkNotNull(task, "task");
		if (!this.inEventLoop()) {
			super.execute(() -> {
				this.thread = Thread.currentThread();
				this.executeTask(task);
			});
		} else {
			super.execute(task);
		}
	}

	public EventLoopGroup getParent() {
		return parent;
	}

	public EventLoop setParent(EventLoopGroup parent) {
		this.parent = parent;
		return this;
	}

	/**
	 * 当前是否运行在EventLoop下
	 * 
	 * @return
	 */
	public boolean inEventLoop(Thread thread) {
		return thread == this.thread;
	}

	/**
	 * 当前是否运行在EventLoop下
	 * 
	 * @return
	 */
	public boolean inEventLoop() {
		return inEventLoop(Thread.currentThread());
	}
}