package com.sample.consumer;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.swak.rabbit.annotation.Subscribe;
import com.swak.rabbit.message.Message;

/**
 * Event 消费
 * 
 * @author lifeng
 */
@Service
public class GoodsEventConsumer {

	/**
	 * 直接返回消费失败
	 * 
	 * @param message
	 * @return
	 */
	@Subscribe(queue = "swak.test.goods")
	public CompletableFuture<Boolean> message(Message message) {
		return CompletableFuture.completedFuture(false);
	}
}