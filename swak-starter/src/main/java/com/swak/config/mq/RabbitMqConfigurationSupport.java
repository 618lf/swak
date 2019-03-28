package com.swak.config.mq;

import java.util.concurrent.Executor;

import com.swak.rabbit.retry.RetryStrategy;

/**
 * 安全的配置项目
 * @author lifeng
 */
public class RabbitMqConfigurationSupport {

	private RetryStrategy retryStrategy;
	private Executor executor;
	
	public RetryStrategy getRetryStrategy() {
		return retryStrategy;
	}
	public Executor getExecutor() {
		return executor;
	}
	public void setRetryStrategy(RetryStrategy retryStrategy) {
		this.retryStrategy = retryStrategy;
	}
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
}