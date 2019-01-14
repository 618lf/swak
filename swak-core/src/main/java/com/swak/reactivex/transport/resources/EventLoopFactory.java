package com.swak.reactivex.transport.resources;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

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
		t.setName(prefix + "-" + counter.incrementAndGet());
		return t;
	}
}
