package com.swak.reactivex.threads;

import com.swak.exception.BaseRuntimeException;
import com.swak.meters.MetricsFactory;
import com.swak.meters.PoolMetrics;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于 Netty 的 HashedWheelTimer 的定时任务
 *
 * @author lifeng
 * @date 2020年8月19日 下午11:53:51
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TimerContext extends HashedWheelTimer implements Context {

    private volatile PoolMetrics metrics;
    private String name;

    public TimerContext(String prefix, boolean daemon, BlockedThreadChecker checker, long maxExecTime,
                        TimeUnit maxExecTimeUnit, long tickDuration, TimeUnit unit) {
        super(new SwakThreadFactory(prefix, daemon, new AtomicInteger(0), checker, maxExecTime, maxExecTimeUnit),
                tickDuration, unit);
        this.name = prefix;
    }

    @Override
    public void execute(Runnable command) {
        throw new BaseRuntimeException("No Support!");
    }

    /**
     * 一次性延迟任务
     *
     * @param task  任务
     * @param delay 延迟
     * @param unit  单位
     * @return 任务
     */
    public ScheduledTimerTask schedule(Runnable task, long delay, TimeUnit unit) {
        ScheduledTimerTask scheduledTimerTask = new ScheduledTimerTask(this, delay, unit, task, false);
        scheduledTimerTask.timeout = this.newTimeout(scheduledTimerTask, delay, unit);
        return scheduledTimerTask;
    }

    /**
     * 周期任务
     *
     * @param task   任务
     * @param period 延迟
     * @param unit   单位
     * @return 任务
     */
    public ScheduledTimerTask scheduleAtFixedRate(Runnable task, long period, TimeUnit unit) {
        ScheduledTimerTask scheduledTimerTask = new ScheduledTimerTask(this, period, unit, task, true);
        scheduledTimerTask.timeout = this.newTimeout(scheduledTimerTask, period, unit);
        return scheduledTimerTask;
    }

    /**
     * 设置监控
     */
    @Override
    public void applyMetrics(MetricsFactory metricsFactory) {
        int nThreads = 1;
        this.metrics = metricsFactory.createScheduleMetrics(name, nThreads);
    }

    /**
     * 延迟任务
     *
     * @author lifeng
     * @date 2020年8月20日 上午12:27:17
     */
    public class ScheduledTimerTask implements TimerTask {

        private Timer timer;
        private Long delay;
        private TimeUnit unit;
        private Runnable command;
        private Timeout timeout;
        private AtomicBoolean isPeriod;

        ScheduledTimerTask(Timer timer, Long delay, TimeUnit unit, Runnable command, boolean isPeriod) {
            this.timer = timer;
            this.delay = delay;
            this.unit = unit;
            this.command = command;
            this.isPeriod = new AtomicBoolean(isPeriod);
        }

        public Timer getTimer() {
            return timer;
        }

        public Timeout getTimeout() {
            return timeout;
        }

        @Override
        public void run(Timeout timeout) {
            Object usageMetric = null;
            if (metrics != null) {
                usageMetric = metrics.begin(null);
            }
            boolean succeeded = executeTask(command);
            if (metrics != null) {
                metrics.end(usageMetric, succeeded);
            }

            // 设置成周期任务
            if (this.isPeriod.get()) {
                this.timeout = newTimeout(this, delay, unit);
            }
        }

        /**
         * 取消周期执行
         */
        public void cancel() {
            this.isPeriod.compareAndSet(true, false);
            Timeout timeout = this.timeout;
            if (timeout != null && !timeout.isCancelled()) {
                timeout.cancel();
            }
        }
    }
}