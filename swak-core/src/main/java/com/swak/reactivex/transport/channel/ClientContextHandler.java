package com.swak.reactivex.transport.channel;

import com.swak.exception.BaseRuntimeException;
import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.options.ClientOptions;

import io.netty.channel.Channel;
import reactor.core.publisher.MonoSink;

/**
 * 代表客户端的连接
 * @author lifeng
 */
public class ClientContextHandler extends CloseableContextHandler {

	final ClientOptions clientOptions;
	final boolean secure;

	ClientContextHandler(ClientOptions options, MonoSink<NettyContext> sink, boolean secure) {
		super(options, sink);
		this.clientOptions = options;
		this.secure = secure;
	}
	
	@Override
	protected void doDropped(Channel channel) {
		channel.close();
		this.fireContextError(new BaseRuntimeException("Channel has been dropped"));
	}
	
	@Override
	protected void doPipeline(Channel ch) {
		this.addSslHandler(ch.pipeline());
	}
	
	/**
	 * 关闭 客户端连接
	 */
	@Override
	public void terminateChannel(Channel channel) {
		this.doDropped(channel);
	}
}