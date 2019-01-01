package com.swak.executor;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

import com.swak.Constants;
import com.swak.utils.Maps;

/**
 * 配置化的执行器
 * 
 * @author lifeng
 */
public class ConfigableExecutor implements Executor, DisposableBean {

	private Map<String, Executor> executors = Maps.newHashMap();
	private Logger logger = LoggerFactory.getLogger(ConfigableExecutor.class);

	/**
	 * 设置执行器 -- 只能在系统启动时设置，启动之后不能设置
	 * 
	 * @param name
	 * @param executor
	 * @return
	 */
	public synchronized ConfigableExecutor setExecutor(String name, Executor executor) {
		Assert.notNull(name, "name does not null");
		Assert.notNull(executor, "executor does not null");
		executors.put(name, executor);
		return this;
	}

	/**
	 * 得到执行器
	 * 
	 * @param name
	 * @return
	 */
	public Executor getExecutor(String name) {
		Executor executor = executors.get(name);
		return executor == null ? executors.get(Constants.default_pool) : executor;
	}

	/**
	 * 代理默认的执行器
	 */
	@Override
	public void execute(Runnable command) {
		this.getExecutor(Constants.default_pool).execute(command);
	}

	// ------------- 配置化 -----------
	public synchronized ConfigableExecutor poolSize(String name, int maxSize) {
		Executor executor = executors.get(name);
		Assert.notNull(name, "executor does not null");
		if (executor instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor _Executor = (ThreadPoolExecutor) executor;
			_Executor.setMaximumPoolSize(maxSize);
		}
		return this;
	}

	public synchronized ConfigableExecutor coreSize(String name, int coreSize) {
		Executor executor = executors.get(name);
		Assert.notNull(name, "executor does not null");
		if (executor instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor _Executor = (ThreadPoolExecutor) executor;
			_Executor.setCorePoolSize(coreSize);
		}
		return this;
	}

	public synchronized ConfigableExecutor timeSeconds(String name, int timeSeconds) {
		Executor executor = executors.get(name);
		Assert.notNull(name, "executor does not null");
		if (executor instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor _Executor = (ThreadPoolExecutor) executor;
			_Executor.setKeepAliveTime(timeSeconds, TimeUnit.SECONDS);
		}
		return this;
	}

	// ------------------- 监控 --------------------------------
	public Map<String, Object> metrics() {
		Map<String, Object> metrics = Maps.newHashMap();
		executors.keySet().stream().forEach(name -> {
			Map<String, Object> one_metrics = Maps.newHashMap();
			Executor executor = executors.get(name);
			if (executor instanceof ThreadPoolExecutor) {
				ThreadPoolExecutor _executor = (ThreadPoolExecutor) executor;
				one_metrics.put("maxPoolSize", _executor.getMaximumPoolSize());
				one_metrics.put("corePoolSize", _executor.getCorePoolSize());
				one_metrics.put("poolSize", _executor.getPoolSize());
				one_metrics.put("taskCount", _executor.getTaskCount());
				one_metrics.put("activeCount", _executor.getActiveCount());
				one_metrics.put("completedTaskCount", _executor.getCompletedTaskCount());
				one_metrics.put("queueSize", _executor.getQueue().size());
				one_metrics.put("largestPoolSize", _executor.getLargestPoolSize());
			}
			metrics.put(name, one_metrics);
		});

		// 默认的线程池 (参数名称需要修改)
		ForkJoinPool commonPool = ForkJoinPool.commonPool();
		Map<String, Object> one_metrics = Maps.newHashMap();
		one_metrics.put("parallelism", commonPool.getParallelism());
		one_metrics.put("commonPoolParallelism", ForkJoinPool.getCommonPoolParallelism());
		one_metrics.put("poolSize", commonPool.getPoolSize());
		one_metrics.put("stealCount", commonPool.getStealCount());
		one_metrics.put("activeThreadCount", commonPool.getActiveThreadCount());
		one_metrics.put("queuedSubmissionCount", commonPool.getQueuedSubmissionCount());
		one_metrics.put("queuedTaskCount", commonPool.getQueuedTaskCount());
		one_metrics.put("runningThreadCount", commonPool.getRunningThreadCount());
		metrics.put("forkjoinpool", one_metrics);

		return metrics;
	}

	public Map<String, Object> metrics(String name) {
		Map<String, Object> one_metrics = Maps.newHashMap();
		Executor executor = executors.get(name);
		if (executor instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor _executor = (ThreadPoolExecutor) executor;
			one_metrics.put("maxPoolSize", _executor.getMaximumPoolSize());
			one_metrics.put("corePoolSize", _executor.getCorePoolSize());
			one_metrics.put("poolSize", _executor.getPoolSize());
			one_metrics.put("taskCount", _executor.getTaskCount());
			one_metrics.put("activeCount", _executor.getActiveCount());
			one_metrics.put("completedTaskCount", _executor.getCompletedTaskCount());
			one_metrics.put("queueSize", _executor.getQueue().size());
			one_metrics.put("largestPoolSize", _executor.getLargestPoolSize());
		}
		return one_metrics;
	}

	/**
	 * 停止服务
	 */
	@Override
	public void destroy() throws Exception {
		executors.values().forEach(ex -> {
			if (ex instanceof ExecutorService) {
				try {
					((ExecutorService) ex).shutdown();
				} catch (Exception e) {
					logger.error("Shutdown Executor Error!", e);
				}
			}
		});
	}
}