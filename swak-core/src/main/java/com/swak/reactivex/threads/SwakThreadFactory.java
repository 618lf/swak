package com.swak.reactivex.threads;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于创建可以监控的线程
 * 
 * @author lifeng
 */
public class SwakThreadFactory implements ThreadFactory {

	// We store all threads in a weak map - we retain this so we can unset context
	// from threads when
	// context is undeployed
	private static final Object FOO = new Object();
	private static Map<SwakThread, Object> weakMap = new WeakHashMap<>();

	private static synchronized void addToMap(SwakThread thread) {
		weakMap.put(thread, FOO);
	}

	private final String prefix;
	private final AtomicInteger threadCount = new AtomicInteger(0);
	private final BlockedThreadChecker checker;
	private final long maxExecTime;
	private final TimeUnit maxExecTimeUnit;

	SwakThreadFactory(String prefix, BlockedThreadChecker checker, long maxExecTime, TimeUnit maxExecTimeUnit) {
		this.prefix = prefix;
		this.checker = checker;
		this.maxExecTime = maxExecTime;
		this.maxExecTimeUnit = maxExecTimeUnit;
	}

	public Thread newThread(Runnable runnable) {
		SwakThread t = new SwakThread(runnable, prefix + threadCount.getAndIncrement(), maxExecTime, maxExecTimeUnit);
		// Vert.x threads are NOT daemons - we want them to prevent JVM exit so embededd
		// user doesn't
		// have to explicitly prevent JVM from exiting.
		if (checker != null) {
			checker.registerThread(t);
		}
		addToMap(t);
		// I know the default is false anyway, but just to be explicit- Vert.x threads
		// are NOT daemons
		// we want to prevent the JVM from exiting until Vert.x instances are closed
		t.setDaemon(false);
		return t;
	}
}