package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.swak.executor.Workers;
import com.swak.reactivex.server.HttpServerProperties;

/**
 * 用于耗时操作
 * @author lifeng
 */
public class ExecutorConfigurationSupport {

	@Autowired
	private HttpServerProperties properties;
	
	public ExecutorConfigurationSupport() {
		APP_LOGGER.debug("Loading Worker Executor");
	}
	
	@Bean
	public Executor workerExecutor() {
		Executor executor = null;
		if (properties.getWorkerThreads() == -1) {
			executor = ForkJoinPool.commonPool();
		} else {
			executor = Executors.newFixedThreadPool(properties.getWorkerThreads(), threadFactory("SWAK-worker"));
		}
		Workers.executor(executor);
		return Workers.executor();
	}
	
	/**
	 * 线程管理器
	 * @param parent
	 * @param prefix
	 * @return
	 */
	ThreadFactory threadFactory(String prefix) {
		AtomicInteger counter = new AtomicInteger();
		return new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				t.setName(prefix + "-" + counter.incrementAndGet());
				return t;
			}
		};
	}
}