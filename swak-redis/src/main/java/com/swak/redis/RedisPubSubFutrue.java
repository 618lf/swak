package com.swak.redis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Redis的异步结果
 * 
 * @author lifeng
 * @date 2020年8月20日 下午1:40:44
 */
public class RedisPubSubFutrue extends CompletableFuture<byte[]> {

	private String channel;

	public RedisPubSubFutrue(String channel) {
		this.channel = channel;
	}

	/**
	 * 尝试处理消息
	 * 
	 * @param channel
	 * @param message
	 */
	public void tryMessage(String channel, byte[] message) {
		if (this.channel.equals(channel) && !this.isDone() && !this.isCancelled()) {
			this.complete(message);
		}
	}

	/**
	 * 获取值，会阻塞当前线程
	 */
	@Override
	public byte[] get() {
		try {
			return super.get();
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取值，会阻塞当前线程
	 */
	@Override
	public byte[] get(long timeout, TimeUnit unit) {
		try {
			return super.get(timeout, unit);
		} catch (Exception e) {
		}
		return null;
	}
}