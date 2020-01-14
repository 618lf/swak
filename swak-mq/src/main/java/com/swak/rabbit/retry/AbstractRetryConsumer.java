package com.swak.rabbit.retry;

import java.util.concurrent.CompletionStage;

import com.swak.rabbit.Constants;
import com.swak.rabbit.annotation.Subscribe;
import com.swak.rabbit.message.Message;

/**
 * 消费重试
 * 
 * @ClassName: RetryConsumer
 * @Description:TODO(描述这个类的作用)
 * @author: lifeng
 * @date: Dec 3, 2019 10:56:37 AM
 */
public abstract class AbstractRetryConsumer {

	/**
	 * 重试 - 消费
	 * 
	 * @Title: retry1s
	 * @Description: TODO(描述)
	 * @author lifeng
	 * @date 2019-12-03 10:59:42
	 */
	@Subscribe(queue = Constants.retry_channel)
	public CompletionStage<Boolean> retry0s(Message message) {
		return this.retry(message);
	}
	
	@Subscribe(queue = Constants.retry1s_channel_cus)
	public CompletionStage<Boolean> retry1s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry5s_channel_cus)
	public CompletionStage<Boolean> retry5s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry10s_channel_cus)
	public CompletionStage<Boolean> retry10s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry30s_channel_cus)
	public CompletionStage<Boolean> retry30s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry60s_channel_cus)
	public CompletionStage<Boolean> retry60s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry120s_channel_cus)
	public CompletionStage<Boolean> retry120s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry180s_channel_cus)
	public CompletionStage<Boolean> retry180s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry240s_channel_cus)
	public CompletionStage<Boolean> retry240s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry300s_channel_cus)
	public CompletionStage<Boolean> retry300s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry360s_channel_cus)
	public CompletionStage<Boolean> retry360s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry420s_channel_cus)
	public CompletionStage<Boolean> retry420s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry480s_channel_cus)
	public CompletionStage<Boolean> retry480s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry540s_channel_cus)
	public CompletionStage<Boolean> retry540s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry600s_channel_cus)
	public CompletionStage<Boolean> retry600s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry1200s_channel_cus)
	public CompletionStage<Boolean> retry1200s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry1800s_channel_cus)
	public CompletionStage<Boolean> retry1800s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry3600s_channel_cus)
	public CompletionStage<Boolean> retry3600s(Message message) {
		return this.retry(message);
	}

	@Subscribe(queue = Constants.retry7200s_channel_cus)
	public CompletionStage<Boolean> retry7200s(Message message) {
		return this.retry(message);
	}

	/**
	 * 子类实现消费任务
	 * 
	 * @Title: retry
	 * @Description: TODO(描述)
	 * @param message
	 * @author lifeng
	 * @date 2019-12-03 11:01:02
	 */
	protected abstract CompletionStage<Boolean> retry(Message message);
}