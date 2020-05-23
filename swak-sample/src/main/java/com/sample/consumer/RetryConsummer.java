package com.sample.consumer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.stereotype.Service;

import com.swak.rabbit.message.Message;
import com.swak.rabbit.retry.AbstractRetryConsumer;

/**
 * 重试消费
 * 
 * @author lifeng
 */
@Service
public class RetryConsummer extends AbstractRetryConsumer {

	@Override
	protected CompletionStage<Boolean> retry(Message message) {
		return CompletableFuture.completedFuture(Boolean.FALSE);
	}
}