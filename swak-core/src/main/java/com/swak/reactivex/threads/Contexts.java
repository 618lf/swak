package com.swak.reactivex.threads;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.resources.LoopResources;

/**
 * 持有所有的线程池的地址
 * 
 * @author lifeng
 */
public class Contexts {

	private static final Object O = new Object();
	private static final Map<Context, Object> contexts = new WeakHashMap<>();
	private final BlockedThreadChecker blockedThreadChecker;

	private static class ContextsHolder {
		private static Contexts instance = new Contexts();
	}

	private Contexts() {
		this.blockedThreadChecker = new BlockedThreadChecker(1000, TimeUnit.MILLISECONDS, 2L * 1000 * 1000000,
				TimeUnit.NANOSECONDS);
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
		WorkerContext context = new WorkerContext(prefix, nThreads, daemon,
				ContextsHolder.instance.blockedThreadChecker, maxExecTime, maxExecTimeUnit);
		contexts.put(context, O);
		return context;
	}

	/**
	 * 创建执行io任务的线程池
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
		SwakLoopResources context = new SwakLoopResources(mode, prefix, select, worker, daemon,
				ContextsHolder.instance.blockedThreadChecker, maxExecTime, maxExecTimeUnit, (eventloop) -> {
					contexts.put(eventloop, O);
				});
		return context;
	}
}