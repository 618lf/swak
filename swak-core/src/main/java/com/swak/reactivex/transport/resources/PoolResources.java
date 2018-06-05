package com.swak.reactivex.transport.resources;

import java.net.SocketAddress;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.swak.reactivex.transport.Disposable;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import reactor.core.publisher.Mono;

public interface PoolResources extends Disposable{

	/**
	 * Create an uncapped {@link PoolResources} to provide automatically for {@link
	 * ChannelPool}.
	 * <p>An elastic {@link PoolResources} will never wait before opening a new
	 * connection. The reuse window is limited but it cannot starve an undetermined volume
	 * of clients using it.
	 *
	 * @param name the channel pool map name
	 *
	 * @return a new {@link PoolResources} to provide automatically for {@link
	 * ChannelPool}
	 */
	static PoolResources elastic(String name) {
		return new DefaultPoolResources(name, SimpleChannelPool::new);
	}
	
	/**
	 * Return an existing or new {@link ChannelPool}. The implementation will take care
	 * of pulling {@link Bootstrap} lazily when a {@link ChannelPool} creation is actually
	 * needed.
	 *
	 * @param address the remote address to resolve for existing or
	 * new {@link ChannelPool}
	 * @param bootstrap the {@link Bootstrap} supplier if a {@link ChannelPool} must be
	 * created
	 * @param onChannelCreate callback only when new connection is made
	 * @return an existing or new {@link ChannelPool}
	 */
	ChannelPool selectOrCreate(SocketAddress address,
			Supplier<? extends Bootstrap> bootstrap,
			Consumer<? super Channel> onChannelCreate,
			EventLoopGroup group);

	@Override
	default void dispose() {
		//noop default
		disposeLater().subscribe();
	}

	/**
	 * Returns a Mono that triggers the disposal of underlying resources when subscribed to.
	 *
	 * @return a Mono representing the completion of resources disposal.
	 **/
	default Mono<Void> disposeLater() {
		return Mono.empty(); //noop default
	}
}
