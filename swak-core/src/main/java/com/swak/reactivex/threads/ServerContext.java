package com.swak.reactivex.threads;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.meters.MetricsFactory;
import com.swak.meters.PoolMetrics;

/**
 * 代码来至： motan 的 StandardThreadExecutor <br/>
 * 
 * 适合服务器的线程池 <br/>
 * 
 * @author lifeng
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class ServerContext extends ThreadPoolExecutor implements Context {

	public static final int DEFAULT_MAX_IDLE_TIME = 60 * 1000; // 1 minutes
	protected AtomicInteger submittedTasksCount; // 正在处理的任务数
	private int maxSubmittedTaskCount; // 最大允许同时处理的任务数

	private volatile PoolMetrics metrics;
	private String name;
	private int nThreads;

	/**
	 * 创建服务线程池
	 * 
	 * @param prefix
	 * @param coreThreads
	 * @param maxThreads
	 * @param queueCapacity
	 * @param checker
	 * @param maxExecTime
	 * @param maxExecTimeUnit
	 */
	public ServerContext(String prefix, int coreThreads, int maxThreads, int queueCapacity,
			BlockedThreadChecker checker, long maxExecTime, TimeUnit maxExecTimeUnit,
			RejectedExecutionHandler handler) {
		this(prefix, coreThreads, maxThreads, DEFAULT_MAX_IDLE_TIME, TimeUnit.MILLISECONDS, queueCapacity, checker,
				maxExecTime, maxExecTimeUnit, handler);
	}

	/**
	 * 创建服务线程池
	 * 
	 * @param prefix
	 * @param coreThreads
	 * @param maxThreads
	 * @param keepAliveTime
	 * @param unit
	 * @param queueCapacity
	 * @param checker
	 * @param maxExecTime
	 * @param maxExecTimeUnit
	 */
	public ServerContext(String prefix, int coreThreads, int maxThreads, long keepAliveTime, TimeUnit unit,
			int queueCapacity, BlockedThreadChecker checker, long maxExecTime, TimeUnit maxExecTimeUnit,
			RejectedExecutionHandler handler) {
		super(coreThreads, maxThreads, keepAliveTime, unit, new ExecutorQueue(),
				new SwakThreadFactory(prefix, true, new AtomicInteger(0), checker, maxExecTime, maxExecTimeUnit),
				new MetricsRejectedExecutionHandler().setHandler(handler));
		((ExecutorQueue) getQueue()).setStandardThreadExecutor(this);

		submittedTasksCount = new AtomicInteger(0);

		// 最大并发任务限制： 队列buffer数 + 最大线程数
		maxSubmittedTaskCount = queueCapacity + maxThreads;
		this.name = prefix;
		this.nThreads = maxThreads;
	}

	/**
	 * 执行将数据
	 */
	@Override
	public void execute(Runnable command) {

		// 记录任务执行的统计
		Object metric = metrics != null ? metrics.submitted() : null;

		// 超过最大的并发任务限制，进行 reject
		// 依赖的LinkedTransferQueue没有长度限制，因此这里进行控制
		int submitted = submittedTasksCount.incrementAndGet();
		if (submitted > maxSubmittedTaskCount) {
			submittedTasksCount.decrementAndGet();
			getRejectedExecutionHandler().rejectedExecution(command, this);
		}

		try {
			super.execute(new MetricsRunnable(metric, command));
		} catch (RejectedExecutionException rx) {
			// there could have been contention around the queue
			if (!((ExecutorQueue) getQueue()).force(command)) {
				submittedTasksCount.decrementAndGet();
				getRejectedExecutionHandler().rejectedExecution(command, this);
			}
		}
	}

	/**
	 * 执行完成之后
	 */
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		submittedTasksCount.decrementAndGet();
	}

	/**
	 * 应用监控
	 */
	@Override
	public void applyMetrics(MetricsFactory metricsFactory) {
		this.metrics = metricsFactory.cteatePoolMetrics(name, nThreads);
	}

	/**
	 * 已经提交的任务
	 * 
	 * @return
	 */
	public int getSubmittedTasksCount() {
		return this.submittedTasksCount.get();
	}

	/**
	 * 最大的可提交的任务
	 * 
	 * @return
	 */
	public int getMaxSubmittedTaskCount() {
		return maxSubmittedTaskCount;
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
	 * LinkedTransferQueue 能保证更高性能，相比与LinkedBlockingQueue有明显提升
	 * 
	 * <pre>
	 * 		1) 不过LinkedTransferQueue的缺点是没有队列长度控制，需要在外层协助控制
	 * </pre>
	 * 
	 * @author maijunsheng
	 *
	 */
	static class ExecutorQueue extends LinkedTransferQueue<Runnable> {
		private static final long serialVersionUID = -265236426751004839L;
		ServerContext threadPoolExecutor;

		public ExecutorQueue() {
			super();
		}

		public void setStandardThreadExecutor(ServerContext threadPoolExecutor) {
			this.threadPoolExecutor = threadPoolExecutor;
		}

		// 注：代码来源于 tomcat
		public boolean force(Runnable o) {
			if (threadPoolExecutor.isShutdown()) {
				throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
			}
			// forces the item onto the queue, to be used if the task is rejected
			return super.offer(o);
		}

		// 注：tomcat的代码进行一些小变更
		public boolean offer(Runnable o) {
			int poolSize = threadPoolExecutor.getPoolSize();

			// we are maxed out on threads, simply queue the object
			if (poolSize == threadPoolExecutor.getMaximumPoolSize()) {
				return super.offer(o);
			}
			// we have idle threads, just add it to the queue
			// note that we don't use getActiveCount(), see BZ 49730
			if (threadPoolExecutor.submittedTasksCount.get() <= poolSize) {
				return super.offer(o);
			}
			// if we have less threads than maximum force creation of a new
			// thread
			if (poolSize < threadPoolExecutor.getMaximumPoolSize()) {
				return false;
			}
			// if we reached here, we need to add it to the queue
			return super.offer(o);
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