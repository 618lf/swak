package com.swak.reactivex.transport;

import java.net.InetSocketAddress;

import com.swak.reactor.publisher.FutureMono;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import reactor.core.publisher.Mono;

/**
 * Netty 服务
 * @author lifeng
 */
public interface NettyContext extends Disposable {

	
	/**
	 * 链接的通道，可以是服务器的通道，也可以是客户端的
	 * @return
	 */
	Channel channel();
	
	
	/**
	 * 关闭通道
	 */
	default void dispose() {
		channel().close();
	}
	
	/**
	 * 是否关闭
	 * @return
	 */
	default boolean isDisposed() {
		return !channel().isActive();
	}
	
	/**
	 * 执行关闭
	 * @return
	 */
	default Mono<Void> onClose(){
		return FutureMono.from(channel().closeFuture());
	}
	
	/**
	 * 地址
	 * @return
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
