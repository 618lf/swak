package com.swak.meters;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.swak.utils.Maps;

/**
 * 线程池的监控
 * 
 * @author lifeng
 */
public interface ExecutorMetrics {


	/**
	 * 获取监控的指标
	 * 
	 * @param executor
	 * @return
	 */
	default Map<String, Object> metrics(Executor executor) {
		Map<String, Object> one_metrics = Maps.newHashMap();
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
}
