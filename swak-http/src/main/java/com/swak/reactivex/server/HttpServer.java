package com.swak.reactivex.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;

import com.swak.reactivex.handler.HttpHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * http 服务器
 * 
 * @author lifeng
 */
public class HttpServer implements WebServer {

	private final static Logger logger = LoggerFactory.getLogger(HttpServer.class);
	private final EventLoopGroup boosGroup = new NioEventLoopGroup();
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();
	private final HttpServerProperties properties;
	private final HttpHandler handler;
	private ServerBootstrap bootstrap;

	public HttpServer(HttpServerProperties properties, HttpHandler handler) {
		this.properties = properties;
		this.handler = handler;
	}

	private void init() {
		// Server 服务启动
		bootstrap = new ServerBootstrap();
		bootstrap.group(boosGroup, workerGroup).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.option(ChannelOption.SO_KEEPALIVE, properties.isSoKeepAlive())
				.childOption(ChannelOption.TCP_NODELAY, properties.isTcpNoDelay())
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.channel(NioServerSocketChannel.class);

		// 设置处理程序
		bootstrap.childHandler(new HttpServerChannelInitializer(handler, properties, boosGroup.next()));
	}

	@Override
	public void start() throws WebServerException {
		try {
			
			// 初始化服务
			this.init();
			
			// 启动服务
			String host = this.properties.getHost();
			int port = this.properties.getPort();
			ChannelFuture future = bootstrap.bind(host, port).sync();
			future.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						logger.debug("Server have success bind to http://" + host + ":" + port);
					} else {
						logger.error("Server fail bind to " + port);
						throw new RuntimeException("Server start fail !", future.cause());
					}
				}
			});
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error("server do not start on port:{}", this.properties.getPort());
		} finally {
			this.stop();
		}
	}

	@Override
	public void stop() throws WebServerException {
		boosGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		logger.debug("shutdown tcp server end.");
	}

	@Override
	public int getPort() {
		return this.properties.getPort();
	}
}