package com.swak.reactivex.transport.resources;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通用的线程创建
 * 
 * @author lifeng
 */
public class EventLoopFactory implements ThreadFactory {

	final boolean daemon;
	final AtomicLong counter;
	final String prefix;

	public EventLoopFactory(boolean daemon, String prefix, AtomicLong counter) {
		this.daemon = daemon;
		this.counter = counter;
		this.prefix = prefix;
	}	

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setDaemon(daemon);
		t.setName(prefix + "thread-" + counter.incrementAndGet());
		return t;
	}
}
