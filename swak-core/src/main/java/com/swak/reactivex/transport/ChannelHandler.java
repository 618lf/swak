package com.swak.reactivex.transport;

/**
 * 处理请求
 * @author lifeng
 * @param <Channel>
 * @param <FullHttpRequest>
 */
@SuppressWarnings("hiding")
public interface ChannelHandler<Channel, Object> {

	/**
	 * 处理请求
	 * @param channel
	 * @param request
	 */
	void handleChannel(Channel channel, Object request);
	
	/**
	 * 处理错误
	 * @param channel
	 * @param request
	 */
	void handleError(Channel channel, Throwable t);
}