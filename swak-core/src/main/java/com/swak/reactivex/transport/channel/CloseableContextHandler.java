package com.swak.reactivex.transport.channel;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	static final Logger log = LoggerFactory.getLogger(CloseableContextHandler.class);

	ChannelFuture f;
	boolean fired;
	
	CloseableContextHandler(NettyOptions<?> options, MonoSink<NettyContext> sink) {
		super(options, sink);
	}
	
	/**
	 * 非常重要，用于通知服务器是否启动成功，或失败
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

		if(future.isDone()){
			try {
				operationComplete((ChannelFuture) future);
			}
			catch (Exception e){
				fireContextError(e);
			}
			return;
		}
		f.addListener(this);
	}
}