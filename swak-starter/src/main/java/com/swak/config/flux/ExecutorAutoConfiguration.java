package com.swak.config.flux;

import static com.swak.Application.APP_LOGGER;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.executor.ConfigableExecutor;
import com.swak.executor.NamedThreadFactory;
import com.swak.executor.Workers;
import com.swak.reactivex.transport.http.server.HttpServerProperties;
import com.swak.reactivex.transport.http.server.ReactiveServer;

/**
 * Worker Executor 配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@ConditionalOnMissingBean(Executor.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableWorkers", matchIfMissing = true)
@EnableConfigurationProperties(HttpServerProperties.class)
public class ExecutorAutoConfiguration {

	@Autowired
	private HttpServerProperties properties;

	public ExecutorAutoConfiguration() {
		APP_LOGGER.debug("Loading Worker Executor");
	}

	/**
	 * 可配置化的线程池
	 * 
	 * @return
	 */
	@Bean
	public ConfigableExecutor configableExecutor() {
		ConfigableExecutor ce = new ConfigableExecutor();
		this.configable(ce);
		Workers.executor(ce);
		return ce;
	}

	private void configable(ConfigableExecutor executor) {

		// 默认
		if (properties.getWorkerThreads() != -1) {
			Executor _executor = Executors.newFixedThreadPool(properties.getWorkerThreads(),
					new NamedThreadFactory("SWAK-worker-default", true));
			executor.setExecutor(Constants.default_pool, _executor);
		}

		// 单个
		Executor _executor = Executors.newFixedThreadPool(1, new NamedThreadFactory("SWAK-worker-single", true));
		executor.setExecutor(Constants.single_pool, _executor);
	}
}