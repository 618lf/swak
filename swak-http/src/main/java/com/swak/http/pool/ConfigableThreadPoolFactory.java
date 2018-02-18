package com.swak.http.pool;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.swak.common.utils.Maps;
import com.swak.common.utils.StringUtils;

public class ConfigableThreadPoolFactory implements ConfigableThreadPool {

	private static int default_threadSize = 2000;
	private static int default_poolSize = 1024;
	private static int default_keepAliveTime = 60 * 5;

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
			defaultExecutor = new ThreadPoolExecutor(default_poolSize, default_threadSize, default_keepAliveTime * 1000,
					TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
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
		poolDefinitions.keySet().stream().forEach(s ->{
			String configs = poolDefinitions.get(s);
			this.createPool(s, configs);
		});
	}
	
	private void createPool(String name, String configs) {
		String[] _configs = configs.split(":");
		ThreadPoolExecutor pool = new ThreadPoolExecutor(
				getDefault(_configs, 1, default_poolSize), 
				getDefault(_configs, 0, default_threadSize), 
				getDefault(_configs, 2, default_keepAliveTime) * 1000,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		this.createPool(name, pool);
	}
	
	private int getDefault(String[] configs, int index, int def) {
		if (configs == null || index >= configs.length) {
			return def;
		}
		return Integer.parseInt(configs[index]);
	}
	
	public static void main(String[] args){
		ConfigableThreadPoolFactory pool = new ConfigableThreadPoolFactory();
		pool.setPoolDefinitions("/admin/system = 2:1:0");
	}
}