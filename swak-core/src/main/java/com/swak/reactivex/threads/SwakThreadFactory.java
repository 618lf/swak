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

	private static final Object FOO = new Object();
	private static Map<SwakThread, Object> weakMap = new WeakHashMap<>();

	private static synchronized void addToMap(SwakThread thread) {
		weakMap.put(thread, FOO);
	}

	private final String prefix;
	private final boolean daemon;
	private final AtomicInteger threadCount;
	private final BlockedThreadChecker checker;
	private final long maxExecTime;
	private final TimeUnit maxExecTimeUnit;

	public SwakThreadFactory(String prefix, boolean daemon, AtomicInteger threadCount) {
		this.prefix = prefix;
		this.daemon = daemon;
		this.checker = null;
		this.threadCount = threadCount;
		this.maxExecTime = 0;
		this.maxExecTimeUnit = null;
	}

	public SwakThreadFactory(String prefix, boolean daemon, AtomicInteger threadCount, BlockedThreadChecker checker,
			long maxExecTime, TimeUnit maxExecTimeUnit) {
		this.prefix = prefix;
		this.daemon = daemon;
		this.checker = checker;
		this.threadCount = threadCount;
		this.maxExecTime = maxExecTime;
		this.maxExecTimeUnit = maxExecTimeUnit;
	}

	public Thread newThread(Runnable runnable) {
		SwakThread t = new SwakThread(runnable, prefix + "thread-" + threadCount.getAndIncrement(), maxExecTime,
				maxExecTimeUnit);
		if (checker != null && maxExecTimeUnit != null && maxExecTime != 0) {
			checker.registerThread(t);
		}
		addToMap(t);
		t.setDaemon(daemon);
		return t;
	}
}