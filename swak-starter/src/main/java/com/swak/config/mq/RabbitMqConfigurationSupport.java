package com.swak.config.mq;

import java.util.function.Function;

import com.swak.rabbit.RabbitMQTemplate;
import com.swak.rabbit.retry.RetryStrategy;

/**
 * rabbit 配置支持
 * 
 * @author lifeng
 */
@SuppressWarnings("deprecation")
public class RabbitMqConfigurationSupport {

	private RetryStrategy retryStrategy;
	private Function<RabbitMQTemplate, Boolean> apply;

	public RetryStrategy getRetryStrategy() {
		return retryStrategy;
	}

	public void setRetryStrategy(RetryStrategy retryStrategy) {
		this.retryStrategy = retryStrategy;
	}

	public Function<RabbitMQTemplate, Boolean> getApply() {
		return apply;
	}

	public void setApply(Function<RabbitMQTemplate, Boolean> apply) {
		this.apply = apply;
	}
}