package com.swak.executor;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.util.Assert;

import com.swak.Constants;
import com.swak.utils.Maps;

/**
 * 配置化的执行器
 * 
 * @author lifeng
 */
public class ConfigableExecutor implements Executor {

	private Map<String, Executor> executors = Maps.newHashMap();

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
}