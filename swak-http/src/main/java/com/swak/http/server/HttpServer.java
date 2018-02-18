package com.swak.http.server;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.swak.http.Filter;
import com.swak.http.Server;
import com.swak.http.Servlet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer implements Server {

	private final static Logger logger = LoggerFactory.getLogger(HttpServer.class);
	private final EventLoopGroup boosGroup = new NioEventLoopGroup();
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();
	private Builder builder;
	private ServerBootstrap bootstrap;
	private MetricRegistry registry;

	public HttpServer(Builder builder) throws Exception {
		this.builder = builder;
		this.registry = new MetricRegistry();
		this.init();
	}

	private void init() throws Exception {
		// Server 服务启动
		bootstrap = new ServerBootstrap();
		bootstrap.group(boosGroup, workerGroup)
		        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
		        .option(ChannelOption.SO_KEEPALIVE, builder.soKeepAlive)
				.childOption(ChannelOption.TCP_NODELAY, builder.tcpNoDelay)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.channel(NioServerSocketChannel.class);

		// 设置处理程序
		bootstrap.childHandler(
				new HttpServerChannelInitializer(new HttpServerContext(registry, builder), boosGroup.next()));

		// 监控
		if (this.builder.startReport) {
			this.startReport();
		}
	}

	private void startReport() {
		ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS).build();
		reporter.start(1, TimeUnit.SECONDS);
	}

	@Override
	public void start() throws Exception {
		try {
			String host = this.builder.getHost();
			int port = this.builder.getPort();
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
			logger.error("server start:{}", builder.port);
		}finally {
			this.stop();
		}
	}

	@Override
	public void stop() throws Exception {
		boosGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		logger.debug("shutdown tcp server end.");
	}

	/**
	 * 参数构造器
	 * 
	 * @author lifeng
	 */
	public static class Builder {

		// 服务器配置
		private int port = 8888;
		private int readTimeout = 120;
		private String host = "localhost";
		private boolean tcpNoDelay = true;
		private boolean soKeepAlive = true;
		private boolean startReport = false;
		private boolean enableGzip = false;
		private boolean enableCors = false;

		// 支持ssl
		private boolean sslOn = false;
		private String certFilePath;
		private String privateKeyPath;
		private String privateKeyPassword;

		// mvc 配置
		private Servlet servlet;
		private Filter filter;

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public boolean isTcpNoDelay() {
			return tcpNoDelay;
		}

		public void setTcpNoDelay(boolean tcpNoDelay) {
			this.tcpNoDelay = tcpNoDelay;
		}

		public boolean isSoKeepAlive() {
			return soKeepAlive;
		}

		public void setSoKeepAlive(boolean soKeepAlive) {
			this.soKeepAlive = soKeepAlive;
		}

		public boolean isStartReport() {
			return startReport;
		}

		public void setStartReport(boolean startReport) {
			this.startReport = startReport;
		}

		public boolean isEnableGzip() {
			return enableGzip;
		}

		public void setEnableGzip(boolean enableGzip) {
			this.enableGzip = enableGzip;
		}

		public boolean isEnableCors() {
			return enableCors;
		}

		public void setEnableCors(boolean enableCors) {
			this.enableCors = enableCors;
		}

		public Servlet getServlet() {
			return servlet;
		}

		public void setServlet(Servlet servlet) {
			this.servlet = servlet;
		}

		public Filter getFilter() {
			return filter;
		}

		public void setFilter(Filter filter) {
			this.filter = filter;
		}

		public void setReadTimeout(int readTimeout) {
			this.readTimeout = readTimeout;
		}

		public int getReadTimeout() {
			return readTimeout;
		}

		public boolean isSslOn() {
			return sslOn;
		}

		public void setSslOn(boolean sslOn) {
			this.sslOn = sslOn;
		}

		public String getCertFilePath() {
			return certFilePath;
		}

		public void setCertFilePath(String certFilePath) {
			this.certFilePath = certFilePath;
		}

		public String getPrivateKeyPath() {
			return privateKeyPath;
		}

		public void setPrivateKeyPath(String privateKeyPath) {
			this.privateKeyPath = privateKeyPath;
		}

		public String getPrivateKeyPassword() {
			return privateKeyPassword;
		}

		public void setPrivateKeyPassword(String privateKeyPassword) {
			this.privateKeyPassword = privateKeyPassword;
		}

	}
}