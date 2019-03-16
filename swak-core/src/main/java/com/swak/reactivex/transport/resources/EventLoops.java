package com.swak.reactivex.transport.resources;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import com.swak.meters.ExecutorMetrics;
import com.swak.utils.Maps;

/**
 * 监控和管理线程池
 * 
 * @author lifeng
 */
public class EventLoops implements ExecutorMetrics {

	private static EventLoops instance = new EventLoops();

	private Map<String, Executor> executors;

	private EventLoops() {
		executors = Maps.newHashMap();
	}

	private void addExecutor(String name, Executor executor) {
		executors.put(name, executor);
	}

	private Executor getExecutor(String name) {
		return executors.get(name);
	}

	public Map<String, Object> metrics() {
		Map<String, Object> metrics = Maps.newHashMap();
		executors.keySet().stream().forEach(name -> {
			Executor executor = executors.get(name);
			metrics.put(name, this.metrics(executor));
		});
		// 默认的线程池 (参数名称需要修改)
		ForkJoinPool commonPool = ForkJoinPool.commonPool();
		metrics.put("forkjoinpool", this.metrics(commonPool));
		return metrics;
	}

	public Map<String, Object> metrics(String name) {
		Executor executor = executors.get(name);
		return this.metrics(executor);
	}

	/**
	 * 唯一实例
	 * 
	 * @return
	 */
	public static EventLoops me() {
		return instance;
	}

	/**
	 * 获取执行器
	 * 
	 * @param name
	 * @return
	 */
	public static Executor fetch(String name) {
		return instance.getExecutor(name);
	}

	/**
	 * 注册管理
	 * 
	 * @param name
	 * @param executor
	 */
	public static void register(String name, Executor executor) {
		if (me().getExecutor(name) != null) {
			throw new RuntimeException("EventLoop Name Exists");
		}
		instance.addExecutor(name, executor);
	}
}