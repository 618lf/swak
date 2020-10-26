package com.swak.reactivex.threads;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

import com.swak.closable.ShutDownHook;
import com.swak.meters.MetricsFactory;
import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.resources.LoopResources;

import io.netty.channel.EventLoop;

/**
 * 持有所有的线程池的地址
 *
 * @author: lifeng
 * @date: 2020/3/29 12:19
 */
public class Contexts {

	private static final Object O = new Object();
	private static final Map<Context, Object> CONTEXTS = new WeakHashMap<>();
	private final BlockedThreadChecker blockedThreadChecker;
	private MetricsFactory metricsFactory;

	private Contexts() {
		this.blockedThreadChecker = new BlockedThreadChecker(1000, TimeUnit.MILLISECONDS, 2L * 1000 * 1000000,
				TimeUnit.NANOSECONDS);
		ShutDownHook.registerShutdownHook(() -> {
			this.blockedThreadChecker.close();
		});
	}

	/**
	 * 设置指标监控工厂
	 * 
	 * @param metricsFactory 设置指标监控工厂
	 */
	public static void setMetricsFactory(MetricsFactory metricsFactory) {
		Holder.instance.metricsFactory = metricsFactory;
		Holder.instance.applyMetricsFactory();
	}

	/**
	 * 适合服务器的线程池模型,优先使用 maxThreader
	 *
	 * @param prefix          线程名称前缀
	 * @param coreThreads     核心线程数
	 * @param maxThreads      最大线程数
	 * @param queueCapacity   队列
	 * @param maxExecTime     最大执行时间
	 * @param maxExecTimeUnit 最大执行时间类型
	 * @param handler         队列满之后的处理方式
	 * @return 线程上下文
	 */
	public static ServerContext createServerContext(String prefix, int coreThreads, int maxThreads, int queueCapacity,
			long maxExecTime, TimeUnit maxExecTimeUnit, RejectedExecutionHandler handler) {
		ServerContext context = new ServerContext(prefix, coreThreads, maxThreads, queueCapacity,
				Holder.instance.blockedThreadChecker, maxExecTime, maxExecTimeUnit, handler);
		Holder.instance.holdContext(context);
		ShutDownHook.registerShutdownHook(() -> {
			context.shutdown();
		});
		return context;
	}

	/**
	 * 适合服务器的线程池模型,优先使用 maxThreader
	 *
	 * @param prefix          线程名称前缀
	 * @param coreThreads     核心线程数
	 * @param maxThreads      最大线程数
	 * @param keepAliveTime   线程保持的时间
	 * @param unit            unit
	 * @param queueCapacity   队列
	 * @param maxExecTime     最大执行时间
	 * @param maxExecTimeUnit 最大执行时间类型
	 * @param handler         队列满之后的处理方式
	 * @return 线程上下文
	 */
	public static ServerContext createServerContext(String prefix, int coreThreads, int maxThreads, long keepAliveTime,
			TimeUnit unit, int queueCapacity, long maxExecTime, TimeUnit maxExecTimeUnit,
			RejectedExecutionHandler handler) {
		ServerContext context = new ServerContext(prefix, coreThreads, maxThreads, keepAliveTime, unit, queueCapacity,
				Holder.instance.blockedThreadChecker, maxExecTime, maxExecTimeUnit, handler);
		Holder.instance.holdContext(context);
		ShutDownHook.registerShutdownHook(() -> {
			context.shutdown();
		});
		return context;
	}

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
	public static WorkerContext createWorkerContext(String prefix, int nThreads, boolean daemon, long maxExecTime,
			TimeUnit maxExecTimeUnit) {
		WorkerContext context = new WorkerContext(prefix, nThreads, daemon, Holder.instance.blockedThreadChecker,
				maxExecTime, maxExecTimeUnit);
		Holder.instance.holdContext(context);
		ShutDownHook.registerShutdownHook(() -> {
			context.shutdown();
		});
		return context;
	}

	/**
	 * 创建执行需时任务的线程池: 可以定义最大队列数以及异常处理方式
	 *
	 * @param prefix          线程名称前缀
	 * @param nThreads        核心线程数
	 * @param daemon          是否后台线程
	 * @param maxExecTime     最大执行时间
	 * @param maxExecTimeUnit 最大执行时间类型
	 * @param maxQueue        最大队列数
	 * @return 线程上下文
	 */
	public static WorkerContext createWorkerContext(String prefix, int nThreads, boolean daemon, long maxExecTime,
			TimeUnit maxExecTimeUnit, int maxQueue) {
		WorkerContext context = new WorkerContext(prefix, nThreads, daemon, Holder.instance.blockedThreadChecker,
				maxExecTime, maxExecTimeUnit, maxQueue);
		Holder.instance.holdContext(context);
		ShutDownHook.registerShutdownHook(() -> {
			context.shutdown();
		});
		return context;
	}

	/**
	 * 创建执行需时任务的线程池: 可以定义最大队列数以及异常处理方式
	 *
	 * @param prefix          线程名称前缀
	 * @param nThreads        核心线程数
	 * @param daemon          是否后台线程
	 * @param maxExecTime     最大执行时间
	 * @param maxExecTimeUnit 最大执行时间类型
	 * @param maxQueue        最大队列数
	 * @param handler         队列满之后的处理方式
	 * @return 线程上下文
	 */
	public static WorkerContext createWorkerContext(String prefix, int nThreads, boolean daemon, long maxExecTime,
			TimeUnit maxExecTimeUnit, int maxQueue, RejectedExecutionHandler handler) {
		WorkerContext context = new WorkerContext(prefix, nThreads, daemon, Holder.instance.blockedThreadChecker,
				maxExecTime, maxExecTimeUnit, maxQueue, handler);
		Holder.instance.holdContext(context);
		ShutDownHook.registerShutdownHook(() -> {
			context.shutdown();
		});
		return context;
	}

	/**
	 * 创建定时执行需时任务的线程池
	 *
	 * @param prefix          线程名称前缀
	 * @param nThreads        核心线程数
	 * @param daemon          是否后台线程
	 * @param maxExecTime     最大执行时间
	 * @param maxExecTimeUnit 最大执行时间类型
	 * @return 线程上下文
	 */
	public static ScheduledContext createScheduledContext(String prefix, int nThreads, boolean daemon, long maxExecTime,
			TimeUnit maxExecTimeUnit) {
		ScheduledContext context = new ScheduledContext(prefix, nThreads, daemon, Holder.instance.blockedThreadChecker,
				maxExecTime, maxExecTimeUnit);
		Holder.instance.holdContext(context);
		ShutDownHook.registerShutdownHook(() -> {
			context.shutdown();
		});
		return context;
	}

	/**
	 * 创建延迟执行任务线程池 -- 不要执行耗时的任务
	 *
	 * @param prefix          线程名称前缀
	 * @param daemon          是否后台线程
	 * @param maxExecTime     最大执行时间
	 * @param maxExecTimeUnit 最大执行时间类型
	 * @param tickDuration    最大执行时间
	 * @param unit            最大执行时间类型
	 * @return 线程上下文
	 */
	public static TimerContext createTimerContext(String prefix, boolean daemon, long maxExecTime,
			TimeUnit maxExecTimeUnit, long tickDuration, TimeUnit unit) {
		TimerContext context = new TimerContext(prefix, daemon, Holder.instance.blockedThreadChecker, maxExecTime,
				maxExecTimeUnit, tickDuration, unit);
		Holder.instance.holdContext(context);
		ShutDownHook.registerShutdownHook(() -> {
			context.stop();
		});
		return context;
	}

	/**
	 * 创建执行io任务的线程池 -- Eventloop 暂时无法做监控
	 *
	 * @param mode            传输的模式
	 * @param select          事件监听线程数
	 * @param worker          事件处理线程数
	 * @param prefix          线程前缀
	 * @param daemon          是否守护线程
	 * @param maxExecTime     最大执行时间
	 * @param maxExecTimeUnit 最大执行时间类型
	 * @return LoopResources
	 */
	public static LoopResources createEventLoopResources(TransportMode mode, Integer select, Integer worker,
			String prefix, boolean daemon, long maxExecTime, TimeUnit maxExecTimeUnit) {
		LoopResources context = LoopResources.create(mode, prefix, select, worker, daemon,
				Holder.instance.blockedThreadChecker, 2, maxExecTimeUnit);
		ShutDownHook.registerShutdownHook(() -> {
			context.dispose();
		});
		return context;
	}

	/**
	 * 返回 EventLoop
	 *
	 * @param eventLoop 事件轮询线程池
	 * @return EventLoopContext
	 */
	public static EventLoopContext createEventLoopContext(EventLoop eventLoop) {
		EventLoopContext context = new EventLoopContext(eventLoop);
		Holder.instance.holdContext(context);
		return context;
	}

	private void applyMetricsFactory() {
		CONTEXTS.forEach((context, o) -> context.applyMetrics(metricsFactory));
	}

	private void holdContext(Context context) {
		CONTEXTS.put(context, O);
		if (metricsFactory != null) {
			context.applyMetrics(metricsFactory);
		}
	}

	private static class Holder {
		private static Contexts instance = new Contexts();
	}
}