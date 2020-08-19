package com.swak.redis;

/**
 * 消息监听
 * 
 * @author lifeng
 * @date 2020年8月19日 下午8:58:01
 */
public interface MessageListener {

	/**
	 * 消息通知
	 * 
	 * @param channel 通道
	 * @param message 消息
	 */
	void onMessage(String channel, byte[] message);
}
