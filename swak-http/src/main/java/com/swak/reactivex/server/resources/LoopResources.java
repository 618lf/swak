package com.swak.reactivex.server.resources;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * EventLoopGroup
 * @author lifeng
 */
public interface LoopResources {
	
	/**
	 * Default worker thread count, fallback to available processor
	 */
	int DEFAULT_IO_WORKER_COUNT = Integer.parseInt(System.getProperty(
			"reactor.ipc.netty.workerCount",
			"" + Math.max(Runtime.getRuntime()
			            .availableProcessors(), 4)));
	
	/**
	 * Default selector thread count, fallback to -1 (no selector thread)
	 */
	int DEFAULT_IO_SELECT_COUNT = Integer.parseInt(System.getProperty(
			"reactor.ipc.netty.selectCount",
			"" + -1));
	
	/**
	 * Create a simple {@link LoopResources} to provide automatically for {@link
	 * EventLoopGroup} and {@link Channel} factories
	 *
	 * @param prefix the event loop thread name prefix
	 *
	 * @return a new {@link LoopResources} to provide automatically for {@link
	 * EventLoopGroup} and {@link Channel} factories
	 */
	static LoopResources create(String prefix) {
		return new DefaultLoopResources(prefix, DEFAULT_IO_SELECT_COUNT,
				DEFAULT_IO_WORKER_COUNT,
				true);
	}
	
	/**
	 * Callback for server channel factory selection.
	 *
	 * @param group the source {@link EventLoopGroup} to assign a loop from
	 *
	 * @return a {@link Class} target for the underlying {@link ServerChannel} factory
	 */
	default Class<? extends ServerChannel> onServerChannel() {
		return NioServerSocketChannel.class;
	}
	
	/**
	 * Create a server select {@link EventLoopGroup} for servers to be used
	 *
	 * @param useNative should use native group if current {@link #preferNative()} is also
	 * true
	 *
	 * @return a new {@link EventLoopGroup}
	 */
	EventLoopGroup onServerSelect();

	/**
	 * Callback for server {@link EventLoopGroup} creation.
	 *
	 * @param useNative should use native group if current {@link #preferNative()} is also
	 * true
	 *
	 * @return a new {@link EventLoopGroup}
	 */
	EventLoopGroup onServer();
	
	/**
	 * Callback for client {@link EventLoopGroup} creation.
	 *
	 * @param useNative should use native group if current {@link #preferNative()} is also
	 * true
	 *
	 * @return a new {@link EventLoopGroup}
	 */
	EventLoopGroup onClient();
}
