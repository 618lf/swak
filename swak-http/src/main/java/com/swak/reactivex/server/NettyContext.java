package com.swak.reactivex.server;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.reactivex.Observable;

/**
 * Hold contextual information for the underlying {@link Channel}
 *
 * @author Stephane Maldini
 * @since 0.6
 */
public interface NettyContext {

	
	/**
	 * Return the underlying {@link Channel}. Direct interaction might be considered
	 * insecure if that affects the
	 * underlying IO processing such as read, write or close or state such as pipeline
	 * handler addition/removal.
	 *
	 * @return the underlying {@link Channel}
	 */
	Channel channel();
	
	
	/**
	 * Cancel or dispose the underlying task or resource.
	 * <p>
	 * Implementations are required to make this method idempotent.
	 */
	default void dispose() {
		channel().close();
	}
	
	/**
	 * Optionally return {@literal true} when the resource or task is disposed.
	 * <p>
	 * Implementations are not required to track disposition and as such may never
	 * return {@literal true} even when disposed. However, they MUST only return true
	 * when there's a guarantee the resource or task is disposed.
	 *
	 * @return {@literal true} when there's a guarantee the resource or task is disposed.
	 */
	default boolean isDisposed() {
		return !channel().isActive();
	}
	
	/**
	 * Return an observing {@link Mono} terminating with success when shutdown
	 * successfully
	 * or error.
	 *
	 * @return a {@link Mono} terminating with success if shutdown successfully or error
	 */
	default Observable<Void> onClose(){
		return Observable.fromFuture(channel().closeFuture());
	}
	
	/**
	 * Return remote address if remote channel {@link NettyContext} otherwise local
	 * address if server selector channel.
	 *
	 * @return remote or local {@link InetSocketAddress}
	 */
	default InetSocketAddress address(){
		Channel c = channel();
		if (c instanceof SocketChannel) {
			return ((SocketChannel) c).remoteAddress();
		}
		if (c instanceof ServerSocketChannel) {
			return ((ServerSocketChannel) c).localAddress();
		}
		if (c instanceof DatagramChannel) {
			InetSocketAddress a = ((DatagramChannel) c).remoteAddress();
			return a != null ? a : ((DatagramChannel)c ).localAddress();
		}
		throw new IllegalStateException("Does not have an InetSocketAddress");
	}
}
