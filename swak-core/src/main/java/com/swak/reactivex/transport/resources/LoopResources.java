package com.swak.reactivex.transport.resources;

import com.swak.reactivex.transport.Disposable;
import com.swak.reactivex.transport.TransportMode;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import reactor.core.publisher.Mono;

/**
 * EventLoopGroup
 * 
 * @author lifeng
 */
public interface LoopResources extends Disposable {

	/**
	 * Create a simple {@link LoopResources} to provide automatically for
	 * {@link EventLoopGroup} and {@link Channel} factories
	 *
	 * @param prefix
	 *            the event loop thread name prefix
	 *
	 * @return a new {@link LoopResources} to provide automatically for
	 *         {@link EventLoopGroup} and {@link Channel} factories
	 */
	static LoopResources create(TransportMode mode, Integer select, Integer worker, String prefix) {
		if (mode != null && TransportMode.EPOLL == mode) {
			return new DefaultEpollLoopResources(prefix, select, worker, true);
		}
		return new DefaultLoopResources(prefix, select, worker, true);
	}

	/**
	 * Callback for server channel factory selection.
	 *
	 * @param group
	 *            the source {@link EventLoopGroup} to assign a loop from
	 *
	 * @return a {@link Class} target for the underlying {@link ServerChannel}
	 *         factory
	 */
	Class<? extends ServerChannel> onServerChannel();

	/**
	 * Create a server select {@link EventLoopGroup} for servers to be used
	 *
	 * @param useNative
	 *            should use native group if current {@link #preferNative()} is also
	 *            true
	 *
	 * @return a new {@link EventLoopGroup}
	 */
	EventLoopGroup onServerSelect();

	/**
	 * Callback for server {@link EventLoopGroup} creation.
	 *
	 * @param useNative
	 *            should use native group if current {@link #preferNative()} is also
	 *            true
	 *
	 * @return a new {@link EventLoopGroup}
	 */
	EventLoopGroup onServer();

	/**
	 * 关闭的时候释放资源
	 */
	default void dispose() {
		// noop default
		disposeLater().subscribe();
	}

	/**
	 * Returns a Mono that triggers the disposal of underlying resources when
	 * subscribed to.
	 *
	 * @return a Mono representing the completion of resources disposal.
	 **/
	default Mono<Void> disposeLater() {
		return Mono.empty(); // noop default
	}
}
