package com.swak.reactivex.server.options;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.function.Consumer;

import com.swak.reactivex.server.resources.LoopResources;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.AttributeKey;

public class ServerOptions extends NettyOptions {

	private final SocketAddress localAddress;
	private EventLoop dateServer;

	/**
	 * Build a new {@link ServerOptions}.
	 */
	protected ServerOptions(ServerOptions.Builder builder) {
		super(builder);
		if (Objects.isNull(builder.host)) {
			this.localAddress = new InetSocketAddress(builder.port);
		} else {
			this.localAddress = new InetSocketAddress(builder.host, builder.port);
		}
	}

	public final SocketAddress getAddress() {
		return localAddress;
	}

	public final EventLoop dateServer() {
		return dateServer;
	}

	/**
	 * 复制一份
	 * 
	 * @return
	 */
	public ServerBootstrap get() {
		ServerBootstrap b = super.get();
		groupAndChannel(b);
		return b;
	}

	final void groupAndChannel(ServerBootstrap bootstrap) {
		LoopResources loops = this.getLoopResources();
		EventLoopGroup selectorGroup = loops.onServerSelect();
		EventLoopGroup elg = loops.onServer();
		bootstrap.group(selectorGroup, elg).channel(loops.onServerChannel());

		// 开启 日期服务
		dateServer = selectorGroup.next();
	}

	public static class Builder extends NettyOptions.Builder {

		private String host;
		private int port;

		protected Builder(ServerBootstrap bootstrapTemplate) {
			super(bootstrapTemplate);
			this.defaultServerOptions(bootstrapTemplate);
		}

		private final void defaultServerOptions(ServerBootstrap bootstrap) {
			bootstrap.option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_BACKLOG, 1000)
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childOption(ChannelOption.SO_RCVBUF, 1024 * 1024).childOption(ChannelOption.SO_SNDBUF, 1024 * 1024)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
		}

		/**
		 * The host on which this server should listen.
		 *
		 * @param host
		 *            The host to bind to.
		 * @return {@code this}
		 */
		public final Builder host(String host) {
			if (Objects.isNull(host)) {
				this.host = "localhost";
			} else {
				this.host = host;
			}
			return this;
		}

		/**
		 * The port on which this server should listen, assuming it should bind to all
		 * available addresses.
		 *
		 * @param port
		 *            The port to listen on.
		 * @return {@code this}
		 */
		public final Builder port(int port) {
			this.port = Objects.requireNonNull(port, "port");
			return this;
		}

		/**
		 * Enable SSL service with a self-signed certificate
		 *
		 * @return {@code this}
		 */
		public final Builder sslSelfSigned() {
			return sslSelfSigned(c -> {
			});
		}

		/**
		 * Enable SSL service with a self-signed certificate and allows extra
		 * parameterization of the self signed {@link SslContextBuilder}. The builder is
		 * then used to invoke {@link #sslContext(SslContext)}.
		 *
		 * @param configurator
		 *            the builder callback to setup the self-signed
		 *            {@link SslContextBuilder}
		 * @return {@code this}
		 */
		public final Builder sslSelfSigned(Consumer<? super SslContextBuilder> configurator) {
			Objects.requireNonNull(configurator, "configurator");
			SelfSignedCertificate ssc;
			try {
				ssc = new SelfSignedCertificate();
				SslContextBuilder builder = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey());
				configurator.accept(builder);
				sslContext(builder.build());
				return this;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Attribute default attribute to the future {@link Channel} connection. They
		 * will be available via
		 * {@link reactor.ipc.netty.NettyInbound#attr(AttributeKey)}.
		 *
		 * @param key
		 *            the attribute key
		 * @param value
		 *            the attribute value
		 * @param <T>
		 *            the attribute type
		 * @return {@code this}
		 * @see Bootstrap#attr(AttributeKey, Object)
		 */
		public final <T> Builder childAttr(AttributeKey<T> key, T value) {
			this.bootstrapTemplate.childAttr(key, value);
			return this;
		}

		/**
		 * Set a {@link ChannelOption} value for low level connection settings like
		 * SO_TIMEOUT or SO_KEEPALIVE. This will apply to each new channel from remote
		 * peer.
		 *
		 * @param key
		 *            the option key
		 * @param value
		 *            the option value
		 * @param <T>
		 *            the option type
		 * @return {@code this}
		 * @see Bootstrap#option(ChannelOption, Object)
		 */
		public final <T> Builder childOption(ChannelOption<T> key, T value) {
			this.bootstrapTemplate.childOption(key, value);
			return this;
		}
	}
}
