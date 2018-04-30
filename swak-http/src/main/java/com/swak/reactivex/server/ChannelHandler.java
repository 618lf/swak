package com.swak.reactivex.server;

/**
 * 处理请求
 * @author lifeng
 * @param <Channel>
 * @param <FullHttpRequest>
 */
@SuppressWarnings("hiding")
public interface ChannelHandler<Channel, Object> {

	void handleChannel(Channel channel, Object request);
}