package com.swak.rpc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

import com.swak.reactivex.transport.options.ClientOptions;
import com.swak.reactivex.transport.resources.LoopResources;
import com.swak.reactivex.transport.tcp.TcpClient;
import com.swak.rpc.api.RpcRequest;
import com.swak.utils.Ints;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

/**
 * 一个连接上下文
 * @author lifeng
 */
public class ConnectorContext implements Connector {

	final TcpClient connector;
	final LoopResources loopResources;
	final RpcClientProperties properties;
	
	/**
	 * 创建连接器
	 * @param address
	 * @param connectCount
	 */
	public ConnectorContext(LoopResources loopResources, RpcClientProperties properties, 
			InetSocketAddress address, int connectCount) {
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

	/**
	 * 建立连接
	 */
	@Override
	public void connect() {
		try {
			connector.connect();
		} catch (InterruptedException e) {}
	}

	/**
	 * 断开连接
	 */
	@Override
	public void disConnect() {
		try {
			connector.close();
		} catch (IOException e) {}
	}

	/**
	 * 发送数据
	 */
	@Override
	public void sent(RpcRequest request) {
		Channel select = null;
		Channel[] channels = connector.getChannels();
		if (channels != null && channels.length == 1) {
			select = channels[0];
		} else if(channels != null && channels.length > 1){
			select = channels[Ints.random(channels.length)];
		}
		this.sent(select, request);
	}
	
	private void sent(Channel channel, RpcRequest request) {
		if (channel != null) {
			channel.writeAndFlush(request);
		}
	}
}