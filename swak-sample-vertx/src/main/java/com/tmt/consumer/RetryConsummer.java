package com.tmt.consumer;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.swak.rabbit.Constants;
import com.swak.rabbit.annotation.Subscribe;
import com.swak.rabbit.message.Message;

/**
 * 重试消费
 * 
 * @author lifeng
 */
@Service
public class RetryConsummer {

	/**
	 * 进入重试队列
	 * 
	 * @param message
	 */
	@Subscribe(queue = Constants.retry_channel)
	public CompletableFuture<Boolean> message(Message message) {
		return CompletableFuture.completedFuture(false);
	}
}