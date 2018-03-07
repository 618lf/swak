package com.swak.http.pool;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.swak.common.utils.Maps;
import com.swak.common.utils.StringUtils;

/**
 * 可配置的线程池
 * java 线程池的策略：
 * 
 * 主要是这四个参数来决定线程池的能力
 * corePoolSize
 * maximumPoolSize
 * workQueue
 * RejectedExecutionHandler
 * 
 * 举例：
 * 如果没有空闲的线程，有如下的情形：
 * 
 * 1. 线程数小于    corePoolSize， 则新增线程执行
 * 2. 线程数大于等于 corePoolSize， workQueue 未满， 则添加到workQueue 中等待
 * 3. 线程数大于等于 corePoolSize， workQueue已满， 单小于 maximumPoolSize， 则新增线程执行（临时工线程）
 * 4. 否则 执行 RejectedExecutionHandler
 * 
 * 
 * 警告： workQueue 不能设置为 max ，不然 maximumPoolSize 不能起作用。
 * 
 * @author lifeng
 */
public class ConfigableThreadPoolExecutor extends ExpectConfig implements ConfigableThreadPool{

	protected static String default_pool_name = "DEFAULT";
	protected static int default_threadSize = 2000;
	protected static int default_poolSize = 1024;
	protected static int default_keepAliveTime = 60;

	private Map<String, ThreadPoolExecutor> executors;
	private ThreadPoolExecutor defaultExecutor;

	/**
	 * 得到一个pool
	 * 
	 * @param request
	 * @return
	 */
	public ThreadPoolExecutor getPool(String path) {
		if (executors != null && !executors.isEmpty()) {
			for (String s : executors.keySet()) {
				if (StringUtils.startsWithIgnoreCase(path, s)) {
					return executors.get(s);
				}
			}
		}
		return this.getDefaultPool();
	}

	/**
	 * 得到默认的pool
	 * 
	 * @return
	 */
	public ThreadPoolExecutor getDefaultPool() {
		if (defaultExecutor == null) {
			synchronized (this) {
				if (defaultExecutor == null) {
					defaultExecutor = new ThreadPoolExecutor(default_poolSize, default_threadSize,
							default_keepAliveTime * 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
					defaultExecutor.allowCoreThreadTimeOut(true);
				}
			}
		}
		return defaultExecutor;
	}

	/**
	 * 创建一个pool
	 * 
	 * @param poolName
	 * @param pool
	 */
	public void createPool(String poolName, ThreadPoolExecutor pool) {
		
		// 如果配置了默认的线程池 -- 处理所有的其他业务,包括 not found
		if (default_pool_name.equalsIgnoreCase(poolName)) {
			defaultExecutor = pool;
			return;
		}
		
		// 其他的是业务的线程池
		if (executors == null) {
			executors = Maps.newHashMap();
		}
		executors.put(poolName, pool);
	}

	@Override
	public List<String> pools() {
		if (executors != null) {
			return executors.keySet().stream().collect(Collectors.toList());
		}
		return null;
	}
	
	/**
	 * url 定义的配置 url 配置是有顺序的
	 * 
	 * @param definitions
	 */
	public void setPoolDefinitions(String definitions) {
		Map<String, String> poolDefinitions = Maps.newOrderMap();
		Scanner scanner = new Scanner(definitions);
		while (scanner.hasNextLine()) {
			String line = StringUtils.clean(scanner.nextLine());
			if (!StringUtils.hasText(line)) {
				continue;
			}
			String[] parts = StringUtils.split(line, '=');
			if (!(parts != null && parts.length == 2)) {
				continue;
			}
			String path = StringUtils.clean(parts[0]);
			String configs = StringUtils.clean(parts[1]);
			if (!(StringUtils.hasText(path) && StringUtils.hasText(configs))) {
				continue;
			}
			poolDefinitions.put(path, configs);
		}
		IOUtils.closeQuietly(scanner);

		// 构建线程池
		poolDefinitions.keySet().stream().forEach(s -> {
			String configs = poolDefinitions.get(s);
			this.createPool(s, configs);
		});
	}

	public void createPool(String name, String configs) {
		String[] _configs = configs.split(":");
		int times = getDefault(_configs, 2, default_keepAliveTime) * 1000;
		ThreadPoolExecutor pool = this.expectPool(name, configs);
		if (pool == null) {
			pool = new ThreadPoolExecutor(getDefault(_configs, 1, default_poolSize),
					getDefault(_configs, 0, default_threadSize), times,
					TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		}
		if (times != 0) {
			pool.allowCoreThreadTimeOut(true);
		}
		this.createPool(name, pool);
	}

	/**
	 * 直接使用 CompletableFuture 来执行
	 */
	@Override
	public void onExecute(String lookupPath, Runnable run) {
		Executor executor = this.getPool(lookupPath);
		CompletableFuture.runAsync(run, executor);
	}
	
	

	/**
	 * 线程池的指标上报
	 */
	@Override
	public void report(MetricRegistry registry) {
		// 其他线程池
		List<String> pools = this.pools();
		if (pools != null) {
			pools.stream().forEach(s ->{
				ThreadPoolExecutor pool = this.getPool(s);
				registry.register("[pool]" + s, (Gauge<String>) () -> {
					StringBuilder sb = new StringBuilder();
					sb.append("Max-").append(pool.getMaximumPoolSize()).append(":");
					sb.append("CorePool-").append(pool.getCorePoolSize()).append(":");
					sb.append("LargestPool-").append(pool.getLargestPoolSize()).append(":");
					sb.append("Pool-").append(pool.getPoolSize()).append(":");
					sb.append("Active-").append(pool.getActiveCount()).append(":");
					sb.append("Queue-").append(pool.getQueue().size()).append(":");
					sb.append("Total-").append(pool.getTaskCount()).append(":");
					sb.append("Completed-").append(pool.getCompletedTaskCount()).append(":");
					sb.append("KeepAlive-").append(pool.getKeepAliveTime(TimeUnit.SECONDS)).append(":");
					return sb.toString();
				});
			});
		}
		
		//默认的线程池
		ThreadPoolExecutor pool = this.getDefaultPool();
		registry.register("[pool]" + default_pool_name, (Gauge<String>) () -> {
			StringBuilder sb = new StringBuilder();
			sb.append("Max-").append(pool.getMaximumPoolSize()).append(":");
			sb.append("CorePool-").append(pool.getCorePoolSize()).append(":");
			sb.append("LargestPool-").append(pool.getLargestPoolSize()).append(":");
			sb.append("Pool-").append(pool.getPoolSize()).append(":");
			sb.append("Active-").append(pool.getActiveCount()).append(":");
			sb.append("Queue-").append(pool.getQueue().size()).append(":");
			sb.append("Total-").append(pool.getTaskCount()).append(":");
			sb.append("Completed-").append(pool.getCompletedTaskCount()).append(":");
			sb.append("KeepAlive-").append(pool.getKeepAliveTime(TimeUnit.SECONDS)).append(":");
			return sb.toString();
		});
	}
}