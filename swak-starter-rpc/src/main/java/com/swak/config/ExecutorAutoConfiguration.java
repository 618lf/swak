package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

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
import com.swak.executor.NamedThreadFactory;
import com.swak.executor.Workers;
import com.swak.rpc.server.RpcServerProperties;

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
	private RpcServerProperties properties;
	
	public ExecutorAutoConfiguration() {
		APP_LOGGER.debug("Loading Worker Executor");
	}
	
	@Bean
	public Executor workerExecutor() {
		Executor executor = null;
		if (properties.getWorkerThreads() == -1) {
			executor = ForkJoinPool.commonPool();
		} else {
			executor = Executors.newFixedThreadPool(properties.getWorkerThreads(), new NamedThreadFactory("SWAK-worker", true));
		}
		Workers.executor(executor);
		return Workers.executor();
	}
}