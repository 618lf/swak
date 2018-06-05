package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.ApplicationProperties;
import com.swak.Constants;
import com.swak.executor.Workers;
import com.swak.reactivex.transport.http.server.HttpServerProperties;

/**
 * Worker Executor 配置
 * 
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 150)
@Order(Ordered.HIGHEST_PRECEDENCE + 150)
@ConditionalOnMissingBean(Executor.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableWorkers", matchIfMissing = true)
@EnableConfigurationProperties(ApplicationProperties.class)
public class ExecutorAutoConfiguration {

	@Autowired
	private HttpServerProperties properties;
	
	public ExecutorAutoConfiguration() {
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
