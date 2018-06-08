package com.swak.rpc.server;

import java.net.InetSocketAddress;
import java.util.function.BiFunction;

import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.options.ServerOptions;
import com.swak.reactivex.transport.tcp.TcpServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import reactor.core.publisher.Mono;

/**
 * RPC 服务器
 * @author lifeng
 */
public class RpcServer extends TcpServer {

	@Override
	public ServerOptions options() {
		return null;
	}
	
	@Override
	public ChannelOperations<?, ?> doHandler(Channel c, ContextHandler contextHandler, Object msg,
			BiFunction<NettyInbound, NettyOutbound, Mono<Void>> ioHandler) {
		return null;
	}

	@Override
	public void accept(ChannelPipeline t, ContextHandler u) {
		
	}

	@Override
	public void start(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler) {
		
	}

	@Override
	public InetSocketAddress getAddress() {
		return null;
	}

	@Override
	public void stop() {
		
	}
}