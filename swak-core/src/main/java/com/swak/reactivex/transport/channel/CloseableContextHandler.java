package com.swak.reactivex.transport.channel;

import java.io.IOException;
import java.util.Objects;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.options.NettyOptions;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import reactor.core.publisher.MonoSink;

/**
 * 监听，服务器启动或关闭
 * @author lifeng
 * @param <CHANNEL>
 */
public abstract class CloseableContextHandler extends ContextHandler
		implements ChannelFutureListener {

	protected ChannelFuture f;
	
	protected CloseableContextHandler(NettyOptions<?> options, MonoSink<NettyContext> sink) {
		super(options, sink);
	}
	
	/**
	 * 启动之后会回调这个方式，通过 sink 将启动消息通知到外面
	 */
	@Override
	public void operationComplete(ChannelFuture f) throws Exception {
		if (!f.isSuccess()) {
			if (f.isCancelled()) {
				log.debug("Cancelled {}", f.channel().toString());
				return;
			}
			if (f.cause() != null) {
				sink.error(f.cause());
			} else {
				sink.error(new IOException("error while connecting to " + f.channel().toString()));
			}
		} else {
			doStarted(f.channel());
		}
	}
	
	/**
	 * 监听启动过程
	 */
	@Override
	public final void setFuture(Future<?> future) {
		Objects.requireNonNull(future, "future");
		if (this.f != null) {
			future.cancel(true);
			return;
		}
		if(log.isDebugEnabled()){
			log.debug("Connecting new channel: {}", future.toString());
		}
		this.f = (ChannelFuture) future;

		if (future.isDone()){
			try {
				operationComplete((ChannelFuture) future);
			}
			catch (Exception e){
				log.error("Connection closed remotely", e);
			}
			return;
		}
		f.addListener(this);
	}
}