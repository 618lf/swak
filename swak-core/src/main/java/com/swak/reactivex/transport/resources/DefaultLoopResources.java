package com.swak.reactivex.transport.resources;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import com.swak.reactor.publisher.FutureMono;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import reactor.core.publisher.Mono;

/**
 * 创建默认的 EventLoopGroup
 * 
 * @author lifeng
 */
public class DefaultLoopResources extends AtomicLong implements LoopResources {

	private static final long serialVersionUID = 1L;

	final String prefix;
	final boolean daemon;
	final int selectCount;
	final int workerCount;
	EventLoopGroup serverLoops;
	EventLoopGroup serverSelectLoops;

	DefaultLoopResources(String prefix, int selectCount, int workerCount, boolean daemon) {
		this.daemon = daemon;
		this.workerCount = workerCount == -1 ? Math.max(Runtime.getRuntime().availableProcessors(), 4) : workerCount;
		this.selectCount = selectCount == -1 ? this.workerCount: selectCount;
		this.prefix = prefix;
	}
	
	@Override
	public Class<? extends ServerChannel> onServerChannel() {
		return NioServerSocketChannel.class;
	}
	
	@Override
	public Class<? extends Channel> onChannel() {
		return NioSocketChannel.class;
	}
	
	@Override
	public EventLoopGroup onServerSelect() {
		if (serverSelectLoops == null) {
			this.serverSelectLoops = new NioEventLoopGroup(selectCount, threadFactory(this, "nio-select"));
		}
		return serverSelectLoops;
	}

	@Override
	public EventLoopGroup onServer() {
		if (this.serverLoops == null) {
			this.serverLoops = new NioEventLoopGroup(workerCount, threadFactory(this, "nio-server"));
		}
		return serverLoops;
	}
	
	/**
	 * 关闭资源
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Mono<Void> disposeLater() {
		serverSelectLoops.shutdownGracefully();
		serverLoops.shutdownGracefully();
		Mono<?> sslMono = FutureMono.from((Future)serverSelectLoops.terminationFuture());
		Mono<?> slMono = FutureMono.from((Future)serverLoops.terminationFuture());
		return Mono.when(sslMono, slMono);
	}
	
	/**
	 * 线程管理器
	 * @param parent
	 * @param prefix
	 * @return
	 */
	ThreadFactory threadFactory(DefaultLoopResources parent, String prefix) {
		return new EventLoopFactory(parent.daemon, parent.prefix + "-" + prefix, parent);
	}
}
