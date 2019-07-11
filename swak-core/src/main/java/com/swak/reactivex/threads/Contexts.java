package com.swak.reactivex.threads;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

import com.swak.meters.MetricsFactory;
import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.resources.LoopResources;

import io.netty.channel.EventLoop;

/**
 * 持有所有的线程池的地址
 * 
 * @author lifeng
 */
public class Contexts {

	private static final Object O = new Object();
	private static final Map<Context, Object> contexts = new WeakHashMap<>();
	private final BlockedThreadChecker blockedThreadChecker;
	private MetricsFactory metricsFactory;

	private Contexts() {
		this.blockedThreadChecker = new BlockedThreadChecker(1000, TimeUnit.MILLISECONDS, 2L * 1000 * 1000000,
				TimeUnit.NANOSECONDS);
	}

	private void applyMetricsFactory() {
		contexts.forEach((context, o) -> {
			context.applyMetrics(metricsFactory);
		});
	}

	private void holdContext(Context context) {
		contexts.put(context, O);
		if (metricsFactory != null) {
			context.applyMetrics(metricsFactory);
		}
	}

	private static class Holder {
		private static Contexts instance = new Contexts();
	}

	/**
	 * 设置指标监控工厂
	 * 
	 * @param metricsFactory
	 */
	public static void setMetricsFactory(MetricsFactory metricsFactory) {
		Holder.instance.metricsFactory = metricsFactory;
		Holder.instance.applyMetricsFactory();
	}

	/**
	 * 创建执行需时任务的线程池
	 * 
	 * @param prefix
	 * @param nThreads
	 * @param maxExecTime
	 * @param maxExecTimeUnit
	 * @return
	 */
	public static WorkerContext createWorkerContext(String prefix, int nThreads, boolean daemon, long maxExecTime,
			TimeUnit maxExecTimeUnit) {
		WorkerContext context = new WorkerContext(prefix, nThreads, daemon, Holder.instance.blockedThreadChecker,
				maxExecTime, maxExecTimeUnit);
		Holder.instance.holdContext(context);
		return context;
	}

	/**
	 * 创建执行需时任务的线程池: 可以定义最大队列数以及异常处理方式
	 * 
	 * @param prefix
	 * @param nThreads
	 * @param maxExecTime
	 * @param maxExecTimeUnit
	 * @return
	 */
	public static WorkerContext createWorkerContext(String prefix, int nThreads, boolean daemon, long maxExecTime,
			TimeUnit maxExecTimeUnit, int maxQueue, RejectedExecutionHandler handler) {
		WorkerContext context = new WorkerContext(prefix, nThreads, daemon, Holder.instance.blockedThreadChecker,
				maxExecTime, maxExecTimeUnit, maxQueue, handler);
		Holder.instance.holdContext(context);
		return context;
	}

	/**
	 * 创建定时执行需时任务的线程池
	 * 
	 * @param prefix
	 * @param nThreads
	 * @param maxExecTime
	 * @param maxExecTimeUnit
	 * @return
	 */
	public static ScheduledContext createScheduledContext(String prefix, int nThreads, boolean daemon, long maxExecTime,
			TimeUnit maxExecTimeUnit) {
		ScheduledContext context = new ScheduledContext(prefix, nThreads, daemon, Holder.instance.blockedThreadChecker,
				maxExecTime, maxExecTimeUnit);
		Holder.instance.holdContext(context);
		return context;
	}

	/**
	 * 创建执行io任务的线程池 -- Eventloop 暂时无法做监控
	 * 
	 * @param mode
	 * @param select
	 * @param worker
	 * @param prefix
	 * @param daemon
	 * @return
	 */
	public static LoopResources createEventLoopResources(TransportMode mode, Integer select, Integer worker,
			String prefix, boolean daemon, long maxExecTime, TimeUnit maxExecTimeUnit) {
		return LoopResources.create(mode, prefix, select, worker, daemon, Holder.instance.blockedThreadChecker, 2,
				maxExecTimeUnit);
	}

	/**
	 * 返回 EventLoop
	 * 
	 * @param eventLoop
	 * @return
	 */
	public static EventLoopContext createEventLoopContext(EventLoop eventLoop) {
		EventLoopContext context = new EventLoopContext(eventLoop);
		Holder.instance.holdContext(context);
		return context;
	}
}