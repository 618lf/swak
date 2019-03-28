package com.swak.config.mq;

import java.util.concurrent.Executor;
import java.util.function.Function;

import com.swak.rabbit.RabbitMQTemplate;
import com.swak.rabbit.retry.RetryStrategy;

/**
 * 安全的配置项目
 * 
 * @author lifeng
 */
public class RabbitMqConfigurationSupport {

	private RetryStrategy retryStrategy;
	private Executor executor;
	private Function<RabbitMQTemplate, Boolean> apply;

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

	public Function<RabbitMQTemplate, Boolean> getApply() {
		return apply;
	}

	public void setApply(Function<RabbitMQTemplate, Boolean> apply) {
		this.apply = apply;
	}
}