package com.swak.fetty.transport;

import com.swak.fetty.transport.eventloop.EventLoopGroup;
import com.swak.fetty.transport.promise.ChannelPromise;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 启动
 * 
 * @author lifeng
 * @date 2020年5月26日 下午1:03:54
 */
@Getter
@Setter
@Accessors(chain = true)
public class AbstractBootStarper {

	EventLoopGroup group;

	public AbstractBootStarper group(EventLoopGroup group) {
		assert group != null;
		this.group = group;
		return this;
	}

	/**
	 * 绑定到:IP端口, 返回异步结果
	 * 
	 * @param ip
	 * @param port
	 * @return
	 * @throws Exception
	 */
	public ChannelPromise bind(String ip, int port) throws Exception {
       return null;
	}
}