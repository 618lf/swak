package com.swak.reactivex.threads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.meters.MetricsFactory;
import com.swak.meters.PoolMetrics;

/**
 * 普通的线程池
 *
 * @author: lifeng
 * @date: 2020/3/29 12:39
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class WorkerContext extends ThreadPoolExecutor implements Context {

    private volatile PoolMetrics metrics;
    private String name;
    private int nThreads;

    /**
     * 创建执行需时任务的线程池
     *
     * @param prefix          线程名称前缀
     * @param nThreads        核心线程数
     * @param daemon          是否后台线程
     * @param maxExecTime     最大执行时间
     * @param maxExecTimeUnit 最大执行时间类型
     * @return 线程上下文
     */
    public WorkerContext(String prefix, int nThreads, boolean daemon, BlockedThreadChecker checker, long maxExecTime,
                         TimeUnit maxExecTimeUnit) {
        super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                new SwakThreadFactory(prefix, daemon, new AtomicInteger(0), checker, maxExecTime, maxExecTimeUnit));
        this.name = prefix;
        this.nThreads = nThreads;
    }

    /**
     * 创建执行需时任务的线程池: 可以定义最大队列数以及异常处理方式
     *
     * @param prefix          线程名称前缀
     * @param nThreads        核心线程数
     * @param daemon          是否后台线程
     * @param maxExecTime     最大执行时间
     * @param maxExecTimeUnit 最大执行时间类型
     * @param maxQueueSize    最大队列数
     * @return 线程上下文
     */
    public WorkerContext(String prefix, int nThreads, boolean daemon, BlockedThreadChecker checker, long maxExecTime,
                         TimeUnit maxExecTimeUnit, int maxQueueSize) {
        super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(maxQueueSize),
                new SwakThreadFactory(prefix, daemon, new AtomicInteger(0), checker, maxExecTime, maxExecTimeUnit),
                new MetricsRejectedExecutionHandler());
        this.name = prefix;
        this.nThreads = nThreads;
    }

    /**
     * 创建执行需时任务的线程池: 可以定义最大队列数以及异常处理方式
     *
     * @param prefix          线程名称前缀
     * @param nThreads        核心线程数
     * @param daemon          是否后台线程
     * @param maxExecTime     最大执行时间
     * @param maxExecTimeUnit 最大执行时间类型
     * @param maxQueueSize    最大队列数
     * @param handler         队列满之后的处理方式
     * @return 线程上下文
     */
    public WorkerContext(String prefix, int nThreads, boolean daemon, BlockedThreadChecker checker, long maxExecTime,
                         TimeUnit maxExecTimeUnit, int maxQueueSize, RejectedExecutionHandler handler) {
        super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(maxQueueSize),
                new SwakThreadFactory(prefix, daemon, new AtomicInteger(0), checker, maxExecTime, maxExecTimeUnit),
                new MetricsRejectedExecutionHandler().setHandler(handler));
        this.name = prefix;
        this.nThreads = nThreads;
    }

    /**
     * 添加指标监控
     */
    @Override
    public void execute(Runnable command) {
        Object metric = metrics != null ? metrics.submitted() : null;
        super.execute(new MetricsRunnable(metric, command));
    }

    @Override
    public void applyMetrics(MetricsFactory metricsFactory) {
        this.metrics = metricsFactory.createPoolMetrics(name, nThreads);
    }

    /**
     * 可监控的
     *
     * @author lifeng
     */
    class MetricsRunnable implements Runnable {
        private final Runnable command;
        private Object metric;

        public MetricsRunnable(Object metric, Runnable command) {
            this.command = command;
            this.metric = metric;
        }

        /**
         * 正常执行代码
         */
        @Override
        public void run() {
            Object usageMetric = null;
            if (metrics != null) {
                usageMetric = metrics.begin(metric);
            }
            boolean succeeded = executeTask(command);
            if (metrics != null) {
                metrics.end(usageMetric, succeeded);
            }
        }

        /**
         * 异常处理
         */
        void rejected() {
            if (metrics != null) {
                metrics.rejected(metric);
            }
        }
    }

    /**
     * 异常处理器
     *
     * @author lifeng
     */
    static class MetricsRejectedExecutionHandler extends AbortPolicy {

        private RejectedExecutionHandler handler;

        public MetricsRejectedExecutionHandler setHandler(RejectedExecutionHandler handler) {
            this.handler = handler;
            return this;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof MetricsRunnable) {
                ((MetricsRunnable) r).rejected();
            }
            if (handler != null) {
                handler.rejectedExecution(r, executor);
            } else {
                super.rejectedExecution(r, executor);
            }
        }
    }
}