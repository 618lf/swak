package com.swak.reactivex.transport.http.client;

import com.swak.reactivex.transport.channel.ContextHandler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * 激活就发送数据
 * @author lifeng
 */
public class HttpClientHandler extends ChannelDuplexHandler {
	
	ContextHandler context;
	
	public HttpClientHandler(ContextHandler contextHandler) {
		this.context = contextHandler;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		HttpClientOperations ops = context.doChannel(ctx.channel(), null);
		if (ops != null) {
			HttpClientOperations old = HttpClientOperations.tryGetAndSet(ctx.channel(), ops);
			if (old != null) {
				return;
			}
			ops.onHandlerStart();
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		HttpClientOperations ops = HttpClientOperations.get(ctx.channel());
		if (ops != null) {
			ops.onChannelClose();
		}
		context.terminateChannel(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		HttpClientOperations ops = HttpClientOperations.get(ctx.channel());
		if (ops != null) {
			ops.onChannelRead(msg);
		} else {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		HttpClientOperations ops = HttpClientOperations.get(ctx.channel());
		if (ops != null) {
			ops.onChannelError(cause);
		}
	}
}