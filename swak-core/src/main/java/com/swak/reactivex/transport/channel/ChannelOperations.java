package com.swak.reactivex.transport.channel;

import java.util.function.BiFunction;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import reactor.core.publisher.Mono;

/**
 * channel operations
 * @author lifeng
 */
public abstract class ChannelOperations<INBOUND extends NettyInbound, OUTBOUND extends NettyOutbound>
		implements NettyInbound, NettyOutbound, Subscriber<Void>, NettyContext {

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
	
	public Channel channel() {
		return channel;
	}
	
	public boolean isKeepAlive() {
		return false;
	}

	/**
	 * 执行请求
	 */
	public void onHandlerStart() {}
	
	/**
	 * 订阅
	 */
	@Override
	public void onSubscribe(Subscription s) {
		s.request(Long.MAX_VALUE);
	}

	/**
	 * 获取数据
	 */
	@Override
	public void onNext(Void t) {}
	
	/**
	 * 主动关闭连接
	 */
	public void onChannelClose() {
		ChannelOperations.remove(channel);
		this.onComplete();
	}
	
	/**
	 * 读取过程中出现异常
	 * @param err
	 */
	public void onChannelError(Throwable err) {
		this.onError(err);
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
	
	// http 协议一个 channel 同时只能处理一个请求，即使是keepalive的，必须等这个请求处理完成后在处理下一个，
	// 所以用这个来记录 request 的生命周期，channel 异常关闭时，正确的释放资源
	protected static final AttributeKey<ChannelOperations<?,?>> OPERATIONS_KEY = AttributeKey
			.newInstance("nettyOperations");
	public static ChannelOperations<?,?> get(Channel ch) {
		return ch.attr(OPERATIONS_KEY).get();
	}
	public static void remove(Channel ch) {
		ch.attr(OPERATIONS_KEY).set(null);
	}
	public static ChannelOperations<?,?> tryGetAndSet(Channel ch, ChannelOperations<?,?> ops) {
		Attribute<ChannelOperations<?,?>> attr = ch.attr(OPERATIONS_KEY);
		for (;;) {
			ChannelOperations<?,?> op = attr.get();
			if (op != null) {
				return op;
			}

			if (attr.compareAndSet(null, ops)) {
				return null;
			}
		}
	}
}