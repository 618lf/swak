package com.swak.reactivex.server.channel;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.server.NettyContext;
import com.swak.reactivex.server.options.NettyOptions;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import reactor.core.publisher.Mono;
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
	MonoSink<NettyContext> sink;
	
	CloseableContextHandler(NettyOptions options, MonoSink<NettyContext> sink) {
		super(options);
		this.sink = sink;
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
	
	/**
	 * Trigger {@link MonoSink#error(Throwable)} that will signal
	 * {@link reactor.ipc.netty.NettyConnector#newHandler(BiFunction)} returned
	 * {@link Mono} subscriber.
	 *
	 * @param t
	 *            error to fail the associated {@link MonoSink}
	 */
	public void fireContextError(Throwable t) {
		if (!fired) {
			fired = true;
			sink.error(t);
		}  else {
			log.error("Error cannot be forwarded to user-facing Mono", t);
		}
	}
	
	/**
	 * Trigger {@link MonoSink#success(Object)} that will signal
	 * {@link reactor.ipc.netty.NettyConnector#newHandler(BiFunction)} returned
	 * {@link Mono} subscriber.
	 *
	 * @param context optional context to succeed the associated {@link MonoSink}
	 */
	public void fireContextActive(NettyContext context) {
		
	}
}
