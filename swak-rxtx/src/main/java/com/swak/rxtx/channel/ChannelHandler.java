package com.swak.rxtx.channel;

/**
 * Channel 处理器
 * 
 * @author lifeng
 */
public class ChannelHandler {

	// 写数据时使用
	ChannelHandler prev;

	// 读数据时使用
	ChannelHandler next;

	/**
	 * 连接处理
	 * 
	 * @param channel
	 */
	public void connect(Channel channel) {
		if (next != null) {
			next.connect(channel);
		}
	}

	/**
	 * 读取数据的处理
	 * 
	 * @param channel
	 * @param data
	 */
	public void read(Channel channel, Object data) {
		if (next != null) {
			next.read(channel, data);
		}
	}

	/**
	 * 读取数据的处理
	 * 
	 * @param channel
	 * @param data
	 */
	public void write(Channel channel, Object data) {
		if (prev != null) {
			prev.write(channel, data);
		}
	}

	/**
	 * 连接处理
	 * 
	 * @param channel
	 */
	public void heartbeat(Channel channel) {
		if (next != null) {
			next.heartbeat(channel);
		}
	}

	/**
	 * 连接处理
	 * 
	 * @param channel
	 */
	public void close(Channel channel) {
		if (next != null) {
			next.close(channel);
		}
	}
}
