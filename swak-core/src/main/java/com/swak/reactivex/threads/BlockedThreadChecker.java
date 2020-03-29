package com.swak.reactivex.threads;

import com.swak.exception.BlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 阻塞检测
 *
 * @author: lifeng
 * @date: 2020/3/28 14:47
 */
public class BlockedThreadChecker {

    /**
     * 阻塞检查任务
     *
     * @author: lifeng
     * @date: 2020/3/28 14:47
     */
    public interface Task {

        /**
         * 开始时间
         *
         * @return 开始时间
         * @author lifeng
         * @date 2020/3/28 14:46
         */
        long startTime();

        /**
         * 最大的执行时间
         *
         * @return 最大的执行时间
         * @author lifeng
         * @date 2020/3/28 14:46
         */
        long maxExecTime();

        /**
         * 时间格式
         *
         * @return 时间格式
         * @author lifeng
         * @date 2020/3/28 14:46
         */
        TimeUnit maxExecTimeUnit();
    }

    private static final Logger log = LoggerFactory.getLogger(BlockedThreadChecker.class);

    private final Map<Thread, Task> threads = new WeakHashMap<>();
    private final ScheduledExecutorService timer;

    BlockedThreadChecker(long interval, TimeUnit intervalUnit, long warningExceptionTime,
                         TimeUnit warningExceptionTimeUnit) {
        timer = new ScheduledThreadPoolExecutor(1, new SwakThreadFactory("Swak-blocked-thread-checker", true, new AtomicInteger()));
        timer.scheduleAtFixedRate(() -> {
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
                    if (val >= timeLimit) {
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
        }, intervalUnit.toMillis(interval), intervalUnit.toMillis(interval), intervalUnit);
    }

    public synchronized void registerThread(Thread thread, Task checked) {
        threads.put(thread, checked);
    }

    public void close() {
        timer.shutdownNow();
    }
}