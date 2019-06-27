package com.swak.reactivex.threads;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.exception.BlockException;

/**
 * 异常阻塞检测
 * 
 * @author lifeng
 */
public class BlockedThreadChecker {

	private static final Logger log = LoggerFactory.getLogger(BlockedThreadChecker.class);

	private static final Object O = new Object();
	private final Map<SwakThread, Object> threads = new WeakHashMap<>();
	private final Timer timer; // Need to use our own timer - can't use event loop for this

	BlockedThreadChecker(long interval, TimeUnit intervalUnit, long warningExceptionTime,
			TimeUnit warningExceptionTimeUnit) {
		timer = new Timer("vertx-blocked-thread-checker", true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				synchronized (BlockedThreadChecker.this) {
					long now = System.nanoTime();
					for (SwakThread thread : threads.keySet()) {
						long execStart = thread.startTime();
						long dur = now - execStart;
						final long timeLimit = thread.getMaxExecTime();
						TimeUnit maxExecTimeUnit = thread.getMaxExecTimeUnit();
						long val = maxExecTimeUnit.convert(dur, TimeUnit.NANOSECONDS);
						if (execStart != 0 && val >= timeLimit) {
							final String message = "Thread " + thread + " has been blocked for " + (dur / 1_000_000)
									+ " ms, time limit is " + TimeUnit.MILLISECONDS.convert(timeLimit, maxExecTimeUnit)
									+ " ms";
							if (warningExceptionTimeUnit.convert(dur, TimeUnit.NANOSECONDS) <= warningExceptionTime) {
								log.warn(message);
							} else {
								BlockException stackTrace = new BlockException("Thread blocked");
								stackTrace.setStackTrace(thread.getStackTrace());
								log.warn(message, stackTrace);
							}
						}
					}
				}
			}
		}, intervalUnit.toMillis(interval), intervalUnit.toMillis(interval));
	}

	public synchronized void registerThread(SwakThread thread) {
		threads.put(thread, O);
	}

	public void close() {
		timer.cancel();
	}
}