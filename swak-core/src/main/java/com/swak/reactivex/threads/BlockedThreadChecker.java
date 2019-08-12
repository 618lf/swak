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

	/**
	 * A checked task.
	 */
	public interface Task {
		long startTime();

		long maxExecTime();

		TimeUnit maxExecTimeUnit();
	}

	private static final Logger log = LoggerFactory.getLogger(BlockedThreadChecker.class);

	private final Map<Thread, Task> threads = new WeakHashMap<>();
	private final Timer timer; // Need to use our own timer - can't use event loop for this

	BlockedThreadChecker(long interval, TimeUnit intervalUnit, long warningExceptionTime,
			TimeUnit warningExceptionTimeUnit) {
		timer = new Timer("Swak-blocked-thread-checker", true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				synchronized (BlockedThreadChecker.this) {
					long now = System.nanoTime();
					for (Map.Entry<Thread, Task> entry : threads.entrySet()) {
						long execStart = entry.getValue().startTime();
						if (execStart == 0) {
							continue;
						}
						long dur = now - execStart;
						final long timeLimit = entry.getValue().maxExecTime();
						TimeUnit maxExecTimeUnit = entry.getValue().maxExecTimeUnit();
						long val = maxExecTimeUnit.convert(dur, TimeUnit.NANOSECONDS);
						if (execStart != 0 && val >= timeLimit) {
							final String message = "Thread" + entry + " has been blocked for " + (dur / 1_000_000)
									+ " ms, time limit is " + TimeUnit.MILLISECONDS.convert(timeLimit, maxExecTimeUnit)
									+ " ms";
							if (warningExceptionTimeUnit.convert(dur, TimeUnit.NANOSECONDS) <= warningExceptionTime) {
								log.warn(message);
							} else {
								BlockException stackTrace = new BlockException("Thread blocked");
								stackTrace.setStackTrace(entry.getKey().getStackTrace());
								log.warn(message, stackTrace);
							}
						}
					}
				}
			}
		}, intervalUnit.toMillis(interval), intervalUnit.toMillis(interval));
	}

	public synchronized void registerThread(Thread thread, Task checked) {
		threads.put(thread, checked);
	}

	public void close() {
		timer.cancel();
	}
}