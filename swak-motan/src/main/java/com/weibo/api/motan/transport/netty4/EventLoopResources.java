package com.weibo.api.motan.transport.netty4;

import com.swak.OS;
import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.resources.LoopResources;
import com.weibo.api.motan.closable.Closable;
import com.weibo.api.motan.closable.ShutDownHook;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

/**
 * 获取客户端或者服务器的线程模型 添加到 ShutDownHook 中
 * 
 * @author lifeng
 */
public class EventLoopResources implements LoopResources, Closable<EventLoopResources> {

	private LoopResources loopResources;

	private EventLoopResources() {
		loopResources = LoopResources.create(transportModeFitOs(), 1, -1, "Motan.");
		ShutDownHook.registerShutdownHook(this);
	}

	@Override
	public Class<? extends ServerChannel> onServerChannel() {
		return loopResources.onServerChannel();
	}

	@Override
	public EventLoopGroup onServerSelect() {
		return loopResources.onServerSelect();
	}

	@Override
	public EventLoopGroup onServer() {
		return loopResources.onServer();
	}

	@Override
	public Class<? extends Channel> onClientChannel() {
		return loopResources.onClientChannel();
	}

	private TransportMode transportModeFitOs() {
		if (OS.me() == OS.linux) {
			return TransportMode.EPOLL;
		}
		return TransportMode.NIO;
	}

	/**
	 * 注册到 ShutDownHook 关闭资源
	 */
	@Override
	public void close() {
		loopResources.dispose();
	}

	/**
	 * 创建 底层循环事件
	 * 
	 * @return
	 */
	public static EventLoopResources me() {
		return new EventLoopResources();
	}
}