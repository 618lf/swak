package com.swak.reactivex.transport.channel;

import java.util.function.BiFunction;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;

import io.netty.channel.Channel;
import reactor.core.publisher.Mono;

/**
 * 提供基本的支持
 * @author lifeng
 */
public abstract class ChannelOperations<INBOUND extends NettyInbound, OUTBOUND extends NettyOutbound>
		implements NettyInbound, NettyOutbound, Subscriber<Void> {

	final protected Logger logger = LoggerFactory.getLogger(ChannelOperations.class);
	final protected BiFunction<? super INBOUND, ? super OUTBOUND, ? extends Mono<Void>> handler;
	final Channel channel;
	final ContextHandler context;

	protected ChannelOperations(Channel channel, BiFunction<? super INBOUND, ? super OUTBOUND, ? extends Mono<Void>> handler,
			ContextHandler context) {
		this.handler = handler;
		this.channel = channel;
		this.context = context;
	}
	
	/**
	 * 对应的连接处理器
	 * @return
	 */
	public ContextHandler context() {
		return context;
	}
	
	/**
	 * 对应的通道
	 */
	public Channel channel() {
		return channel;
	}
	
	/**
	 * 是否保持连接
	 * @return
	 */
	public boolean isKeepAlive() {
		return false;
	}

	/**
	 * 执行请求
	 */
	public abstract void onHandlerStart();
	
	/**
	 * 订阅
	 */
	@Override
	public void onSubscribe(Subscription s) {
		s.request(Long.MAX_VALUE);
	}

	/**
	 * 返回值是 void, 不会执行此方法
	 */
	@Override
	public void onNext(Void t) {}
	
	/**
	 * 服务器才需要处理
	 */
	@Override
	public void onError(Throwable t) {}

	/**
	 * 服务器才需要处理
	 */
	@Override
	public void onComplete() {}
	
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