package com.swak.paxos.transport.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.transport.ChannelState;

/**
 * 基础的服务
 * 
 * @author lifeng
 * @date 2020年12月26日 下午7:55:49
 */
public abstract class AbstractClient {
	protected Logger logger = LoggerFactory.getLogger(AbstractClient.class);
	protected volatile ChannelState state = ChannelState.UNINIT;

	/**
	 * 服务的状态
	 * 
	 * @return
	 */
	public boolean isAvailable() {
		return state.isAliveState();
	}

	/**
	 * 是否已经关闭
	 * 
	 * @return
	 */
	public boolean isClosed() {
		return state.isCloseState();
	}

}
