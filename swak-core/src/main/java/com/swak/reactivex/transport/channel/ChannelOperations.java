package com.swak.reactivex.transport.channel;

import java.util.function.BiFunction;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;

import io.netty.channel.Channel;
import reactor.core.publisher.Mono;

/**
 * channel operations
 * @author lifeng
 */
public abstract class ChannelOperations<INBOUND extends NettyInbound, OUTBOUND extends NettyOutbound>
		implements NettyInbound, NettyOutbound, Subscriber<Void>, NettyContext {

	final protected BiFunction<? super INBOUND, ? super OUTBOUND, ? extends Mono<Void>> handler;
	final Channel channel;
	final ContextHandler context;

	protected ChannelOperations(Channel channel, BiFunction<? super INBOUND, ? super OUTBOUND, ? extends Mono<Void>> handler,
			ContextHandler context) {
		this.handler = handler;
		this.channel = channel;
		this.context = context;
	}
	
	public Channel channel() {
		return channel;
	}

	@Override
	public void onSubscribe(Subscription s) {
		s.request(Long.MAX_VALUE);
	}

	@Override
	public void onNext(Void t) {}
	
	/**
	 * 执行请求
	 */
	protected void onHandlerStart() {
		context.fireContextActive(this);
	}

	/**
	 * A {@link ChannelOperations} factory
	 */
	@FunctionalInterface
	public interface OnNew {

		/**
		 * 构建 ChannelOperations
		 * @param c
		 * @param contextHandler
		 * @param msg
		 * @return
		 */
		ChannelOperations<?, ?> create(Channel c, ContextHandler contextHandler, Object msg);
	}
}