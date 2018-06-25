package com.swak.reactivex.transport.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.swak.reactivex.transport.options.ClientOptions;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * 客户端连接器 -- 用于客户端的处理场景
 * @author lifeng
 */
public abstract class TcpClient extends ChannelInitializer<Channel> implements Closeable {
	
	final InetSocketAddress address;
	final int connectCount;
	private volatile Channel[] channels;

	public TcpClient(InetSocketAddress address, int connectCount) {
		this.address = address;
		this.connectCount = connectCount;
	}
	
	@Override
	protected void initChannel(Channel ch) throws Exception {
       this.accept(ch.pipeline());		
	}

	/**
	 * 配置客户端
	 * @return
	 */
	public abstract ClientOptions options();
	
	/**
	 * 配置管道
	 * @param pipeline
	 */
	public abstract void accept(ChannelPipeline pipeline);
	
	/**
	 * 建立连接
	 * @throws InterruptedException
	 */
	public void connect() throws InterruptedException {
		
		/**
		 * 配置信息
		 */
		ClientOptions options = options();
		
		/**
		 * 建立连接
		 */
		Bootstrap bootstrap = options.get().remoteAddress(address).handler(this);
		
		/**
		 * 创建的连接
		 */
		Channel[] newChannels = new Channel[connectCount];
		for (int i = 0; i < connectCount; i++) {
			newChannels[i] = bootstrap.connect(address).sync().channel();
		}
		
		Channel[] old = channels;
		channels = newChannels;

		if (old != null) {
			for (int i = 0; i < old.length; i++) {
				try {
					old[i].close();
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	/**
	 * 关闭连接
	 */
	@Override
	public void close() throws IOException {

		if (channels == null) {
			return;
		}

		for (int i = 0; i < channels.length; i++) {
			try {
				channels[i].close();
			} catch (Exception e) {}
		}

		channels = null;
	}
}