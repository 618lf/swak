package com.swak.reactivex.transport.options;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.swak.reactivex.transport.resources.LoopResources;
import com.swak.reactivex.transport.resources.PoolResources;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.resolver.AddressResolverGroup;
import io.netty.util.NetUtil;
import reactor.core.Exceptions;

/**
 * 
 * 客户端配置项
 * 
 * @author lifeng
 */
public class ClientOptions extends NettyOptions<Bootstrap> {
	
	/**
	 * Creates a builder for {@link ClientOptions ClientOptions}
	 *
	 * @param <BUILDER> A ClientOptions.Builder subclass
	 * @return a new ClientOptions builder
	 */
	public static ClientOptions.Builder builder() {
		return builder(new Bootstrap());
	}

	/**
	 * Creates a builder for {@link ClientOptions ClientOptions}
	 *
	 * @param bootstrap the bootstrap reference to use
	 * @param <BUILDER> A ClientOptions.Builder subclass
	 * @return a new ClientOptions builder
	 */
	public static ClientOptions.Builder builder(Bootstrap bootstrap) {
		return new ClientOptions.Builder(bootstrap);
	}
	
	
	/**
	 * Client connection pool selector
	 */
	private final PoolResources poolResources;
	private final Supplier<? extends SocketAddress> connectAddress;
	
	protected ClientOptions(Builder builder) { 
		super(builder);
		if (Objects.isNull(builder.connectAddress)) {
			if (builder.port >= 0) {
				if (Objects.isNull(builder.host)) {
					this.connectAddress = () -> new InetSocketAddress(NetUtil.LOCALHOST, builder.port);
				}
				else {
					this.connectAddress = () -> InetSocketAddressUtil.createUnresolved(builder.host, builder.port);
				}
			}
			else {
				this.connectAddress = null;
			}
		}
		else {
			this.connectAddress = builder.connectAddress;
		}
		this.poolResources = builder.poolResources;
	}
	
	@Override
	public Bootstrap get() {
		Bootstrap b = super.get();
		groupAndChannel(b);
		return b;
	}
	
	public final SocketAddress getAddress() {
		return connectAddress == null ? null : connectAddress.get();
	}
	
	@SuppressWarnings("unchecked")
	final void groupAndChannel(Bootstrap bootstrap) {
		LoopResources loops = Objects.requireNonNull(getLoopResources(), "loopResources");
		EventLoopGroup elg = loops.onClient();
		if (this.poolResources != null && elg instanceof Supplier) {
			bootstrap.group(((Supplier<EventLoopGroup>) elg).get());
		}
		else {
			bootstrap.group(elg);
		}
		bootstrap.channel(loops.onChannel());
	}
	
	/**
	 * Get the configured Pool Resources if any
	 *
	 * @return an eventual {@link PoolResources}
	 */
	public final PoolResources getPoolResources() {
		return this.poolResources;
	}
	
	protected InetSocketAddress createInetSocketAddress(String hostname, int port, boolean resolve) {
		return InetSocketAddressUtil.createInetSocketAddress(hostname, port, resolve);
	}

	public static class Builder extends NettyOptions.Builder<Bootstrap> {

		private PoolResources poolResources;
		private boolean poolDisabled = false;
		private Supplier<? extends SocketAddress> connectAddress;
		private String host;
		private int port = -1;

		protected Builder(Bootstrap bootstrapTemplate) {
			super(bootstrapTemplate);
			defaultClientOptions(bootstrapTemplate);
		}

		private void defaultClientOptions(Bootstrap bootstrap) {
			bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000).option(ChannelOption.AUTO_READ, false)
					.option(ChannelOption.SO_RCVBUF, 1024 * 1024).option(ChannelOption.SO_SNDBUF, 1024 * 1024);
		}

		/**
		 * Assign an {@link AddressResolverGroup}.
		 *
		 * @param resolver
		 *            the new {@link AddressResolverGroup}
		 * @return {@code this}
		 */
		public final Builder resolver(AddressResolverGroup<?> resolver) {
			Objects.requireNonNull(resolver, "resolver");
			this.bootstrapTemplate.resolver(resolver);
			return this;
		}

		/**
		 * Configures the {@link ChannelPool} selector for the socket. Will effectively
		 * enable client connection-pooling.
		 *
		 * @param poolResources
		 *            the {@link PoolResources} given an {@link InetSocketAddress}
		 * @return {@code this}
		 */
		public final Builder poolResources(PoolResources poolResources) {
			this.poolResources = Objects.requireNonNull(poolResources, "poolResources");
			this.poolDisabled = false;
			return this;
		}

		/**
		 * Disable current {@link #poolResources}
		 *
		 * @return {@code this}
		 */
		public Builder disablePool() {
			this.poolResources = null;
			this.poolDisabled = true;
			return this;
		}

		public final boolean isPoolDisabled() {
			return poolDisabled;
		}

		public final boolean isPoolAvailable() {
			return this.poolResources != null;
		}
		
		/**
		 * Enable default sslContext support
		 *
		 * @return {@code this}
		 */
		public final Builder sslSupport() {
			return sslSupport(c -> {
			});
		}

		/**
		 * Enable default sslContext support and enable further customization via the
		 * passed configurator. The builder will then produce the {@link SslContext} to
		 * be passed to {@link #sslContext(SslContext)}.
		 *
		 * @param configurator
		 *            builder callback for further customization.
		 * @return {@code this}
		 */
		public final Builder sslSupport(Consumer<? super SslContextBuilder> configurator) {
			Objects.requireNonNull(configurator, "configurator");
			try {
				SslContextBuilder builder = SslContextBuilder.forClient();
				configurator.accept(builder);
				sslContext(builder.build());
				return this;
			} catch (Exception sslException) {
				throw Exceptions.bubble(sslException);
			}
		}

		/**
		 * The host to which this client should connect.
		 *
		 * @param host
		 *            The host to connect to.
		 * @return {@code this}
		 */
		public final Builder host(String host) {
			if (Objects.isNull(host)) {
				this.host = NetUtil.LOCALHOST.getHostAddress();
			} else {
				this.host = host;
			}
			return this;
		}

		/**
		 * The port to which this client should connect.
		 *
		 * @param port
		 *            The port to connect to.
		 * @return {@code this}
		 */
		public final Builder port(int port) {
			this.port = Objects.requireNonNull(port, "port");
			return this;
		}

		/**
		 * The address to which this client should connect.
		 *
		 * @param connectAddressSupplier
		 *            A supplier of the address to connect to.
		 * @return {@code this}
		 */
		public final Builder connectAddress(Supplier<? extends SocketAddress> connectAddressSupplier) {
			this.connectAddress = Objects.requireNonNull(connectAddressSupplier, "connectAddressSupplier");
			return this;
		}
		
		/**
		 * 创建 ClientOptions
		 * @return
		 */
		public ClientOptions build() {
			return new ClientOptions(this);
		}
	}
}