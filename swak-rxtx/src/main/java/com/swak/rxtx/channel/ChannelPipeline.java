package com.swak.rxtx.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 管道 -- 只处理一个方向即可: 我就是托底
 * 
 * @author lifeng
 */
public class ChannelPipeline extends ChannelHandler {

	/**
	 * 用于消息调试
	 */
	private static Logger logger = LoggerFactory.getLogger(Channel.class);

	// 记录双向链表
	private ChannelHandler head;
	private ChannelHandler tail;
	private Channel channel;

	/**
	 * 创建执行链（永远保证this是第一个）
	 */
	public ChannelPipeline(Channel channel) {
		this.channel = channel;
		this.head = this;
		this.tail = this;
	}

	/**
	 * 通道
	 * 
	 * @return
	 */
	public Channel channel() {
		return this.channel;
	}

	/**
	 * 添加到头部
	 * 
	 * @param handler
	 * @return
	 */
	public ChannelPipeline add(ChannelHandler handler) {

		if (logger.isDebugEnabled()) {
			logger.debug("设备：[{}], 添加处理器：[{}]", this.channel.comm(), handler.getClass().getSimpleName());
		}

		handler.prev = tail;
		tail.next = handler;
		tail = handler;
		return this;
	}

	/**
	 * 触发连接事件
	 */
	public void fireConnectEvent(Channel channel) {
		this.head.connect(channel);
	}

	/**
	 * 触发读取事件
	 */
	public void fireReadEvent(Channel channel, Object data) {
		this.head.read(channel, data);
	}

	/**
	 * 触发写事件
	 */
	public void fireWriteEvent(Channel channel, Object data) {
		this.head.write(channel, data);
	}

	/**
	 * 触发心跳事件
	 */
	public void fireHeartbeatEvent(Channel channel) {
		this.head.heartbeat(channel);
	}

	/**
	 * 触发关闭事件
	 */
	public void fireCloseEvent(Channel channel) {
		this.head.close(channel);
	}

	/**
	 * 写数据
	 */
	@Override
	public void write(Channel channel, Object data) {
		if (data instanceof byte[]) {
			channel.writeAndFlush((byte[]) data);
		}
	}
}
