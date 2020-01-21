package com.swak.rxtx.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Channel 处理器
 * 
 * @author lifeng
 */
public class ChannelHandler {

	/**
	 * 用于消息调试
	 */
	private static Logger logger = LoggerFactory.getLogger(Channel.class);

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
		if (logger.isDebugEnabled()) {
			logger.debug("处理设备：[{}]连接事件, 处理器:[{}]", channel.comm(), this.getClass().getSimpleName());
		}
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
		if (logger.isDebugEnabled()) {
			logger.debug("处理设备：[{}]读取数据事件, 处理器:[{}]", channel.comm(), this.getClass().getSimpleName());
		}
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
		if (logger.isDebugEnabled()) {
			logger.debug("处理设备：[{}]写数据事件, 处理器:[{}]", channel.comm(), this.getClass().getSimpleName());
		}
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
		if (logger.isDebugEnabled()) {
			logger.debug("处理设备：[{}]心跳事件, 处理器:[{}]", channel.comm(), this.getClass().getSimpleName());
		}
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
		if (logger.isDebugEnabled()) {
			logger.debug("处理设备：[{}]关闭事件, 处理器:[{}]", channel.comm(), this.getClass().getSimpleName());
		}
		if (next != null) {
			next.close(channel);
		}
	}
}
