package com.swak.http.resource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.asynchttpclient.netty.channel.ChannelManager;

import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.EventLoopContext;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;

/**
 * 自定义执行链
 * 
 * @author lifeng
 */
public class SharedNettyCustomizer implements Consumer<Channel> {

	private final ConcurrentMap<EventLoop, EventLoopContext> handlers = new ConcurrentHashMap<>();

	@Override
	public void accept(Channel channel) {
		channel.pipeline().addBefore(ChannelManager.AHC_HTTP_HANDLER, "SharedChannel",
				new SharedChannelHandler(channel));
	}

	/**
	 * 选择执行的上下文
	 * 
	 * @param eventLoop
	 * @return
	 */
	private EventLoopContext chooseEventLoop(EventLoop eventLoop) {
		if (handlers.containsKey(eventLoop)) {
			return handlers.get(eventLoop);
		}
		handlers.putIfAbsent(eventLoop, Contexts.createEventLoopContext(eventLoop));
		return handlers.get(eventLoop);
	}

	/**
	 * 让后续代码可监控
	 * 
	 * @author lifeng
	 */
	class SharedChannelHandler extends ChannelInboundHandlerAdapter {

		EventLoopContext context;

		public SharedChannelHandler(Channel channel) {
			context = chooseEventLoop(channel.eventLoop());
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			context.execute(() -> {
				try {
					super.channelRead(ctx, msg);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
}