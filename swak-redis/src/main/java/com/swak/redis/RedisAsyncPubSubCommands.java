package com.swak.redis;

import java.util.concurrent.CompletionStage;

/**
 * 异步发布订阅
 * 
 * @author lifeng
 * @date 2020年8月19日 下午8:29:15
 */
public interface RedisAsyncPubSubCommands<K, V> {

	/**
	 * 添加监听
	 * 
	 * @param listener
	 */
	void listen(MessageListener listener);

	/**
	 * 发布消息
	 * 
	 * @param channel
	 * @param message
	 * @return
	 */
	CompletionStage<Long> publish(byte[] channel, byte[] message);

	/**
	 * 订阅消息，并设置监听器
	 * 
	 * @param listener
	 * @param channel
	 * @return
	 */
	CompletionStage<Void> subscribe(MessageListener listener, byte[]... channel);

	/**
	 * 订阅消息
	 * 
	 * @param channel
	 * @return
	 */
	CompletionStage<Void> subscribe(byte[]... channel);

	/**
	 * 取消订阅, 并删除监听器
	 * 
	 * @param channels
	 * @return
	 */
	CompletionStage<Void> unSubscribe(MessageListener listener, byte[]... channels);

	/**
	 * 取消订阅
	 * 
	 * @param channels
	 * @return
	 */
	CompletionStage<Void> unSubscribe(byte[]... channels);

	/**
	 * 订阅一次，等到消息到达
	 * 
	 * @param channel
	 * @return
	 */
	RedisPubSubFutrue subscribeOnce(byte[] channel);
	
	/**
	 * 订阅一次，等到消息到达
	 * 
	 * @param channel
	 * @return
	 */
	void unSubscribe(RedisPubSubFutrue future);
}
