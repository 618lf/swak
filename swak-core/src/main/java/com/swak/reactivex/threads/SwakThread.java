package com.swak.reactivex.threads;

import java.util.concurrent.TimeUnit;

import io.netty.util.concurrent.FastThreadLocalThread;

/**
 * 带有监控功能的线程
 * 
 * @author lifeng
 */
public class SwakThread extends FastThreadLocalThread {

	private final long maxExecTime;
	private final TimeUnit maxExecTimeUnit;
	private long execStart;

	public SwakThread(Runnable target, String name, long maxExecTime, TimeUnit maxExecTimeUnit) {
	    super(target, name);
	    this.maxExecTime = maxExecTime;
	    this.maxExecTimeUnit = maxExecTimeUnit;
	  }

	public final void executeStart() {
		execStart = System.nanoTime();
	}

	public final void executeEnd() {
		execStart = 0;
	}

	public long startTime() {
		return execStart;
	}

	public long getMaxExecTime() {
		return maxExecTime;
	}

	public TimeUnit getMaxExecTimeUnit() {
		return maxExecTimeUnit;
	}
}