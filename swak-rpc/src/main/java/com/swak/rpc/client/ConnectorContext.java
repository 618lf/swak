package com.swak.rpc.client;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

import com.swak.reactivex.transport.options.ClientOptions;
import com.swak.reactivex.transport.resources.LoopResources;
import com.swak.reactivex.transport.tcp.TcpClient;

import io.netty.channel.ChannelPipeline;

/**
 * 
 * @author lifeng
 */
public class ConnectorContext {

	final TcpClient connector;
	final LoopResources loopResources;
	final RpcClientProperties properties;
	
	/**
	 * 创建连接器
	 * @param address
	 * @param connectCount
	 */
	public ConnectorContext(LoopResources loopResources, RpcClientProperties properties, InetSocketAddress address, int connectCount) {
		this.loopResources = loopResources;
		this.properties = properties;
		connector = new TcpClient(address, connectCount) {
			@Override
			public ClientOptions options() {
				return ConnectorContext.this.options((options) -> {
					options.loopResources(loopResources);
				});
			}

			@Override
			public void accept(ChannelPipeline pipeline) {
				ConnectorContext.this.accept(pipeline);
			}
		};
	}
	
	/**
	 * 配置options
	 */
	private RpcClientOptions options(Consumer<? super RpcClientOptions.Builder> options) {
		RpcClientOptions.Builder serverOptionsBuilder = RpcClientOptions.builder();
		options.accept(serverOptionsBuilder);
		return serverOptionsBuilder.build();
	}
	
	/**
	 * 配置管道
	 * @param pipeline
	 */
	private void accept(ChannelPipeline pipeline) {}
}