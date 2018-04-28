package com.swak.reactivex.server.options;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.swak.reactivex.server.context.NettyContext;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.util.AttributeKey;

/**
 * Netty
 * 
 * @author lifeng
 */
public class NettyOptions {
	
	private final ServerBootstrap                  bootstrapTemplate;
	private final boolean                          preferNative;
	private final SslContext                       sslContext;
	private final long                             sslHandshakeTimeoutMillis;
	private final long                             sslCloseNotifyFlushTimeoutMillis;
	private final long                             sslCloseNotifyReadTimeoutMillis;
	protected final Consumer<? super Channel>      afterChannelInit;
	protected final Consumer<? super NettyContext> afterNettyContextInit;
	private final Predicate<? super Channel>       onChannelInit;
	
	protected NettyOptions(NettyOptions.Builder builder) {
		this.bootstrapTemplate = builder.bootstrapTemplate;
		this.preferNative = builder.preferNative;
		this.sslContext = builder.sslContext;
		this.sslHandshakeTimeoutMillis = builder.sslHandshakeTimeoutMillis;
		this.sslCloseNotifyFlushTimeoutMillis = builder.sslCloseNotifyFlushTimeoutMillis;
		this.sslCloseNotifyReadTimeoutMillis = builder.sslCloseNotifyReadTimeoutMillis;
		this.afterNettyContextInit = builder.afterNettyContextInit;
		this.onChannelInit = builder.onChannelInit;

		Consumer<? super Channel> afterChannel = builder.afterChannelInit;
		if (afterChannel != null && builder.channelGroup != null) {
			this.afterChannelInit = ((Consumer<Channel>) builder.channelGroup::add)
					.andThen(afterChannel);
		}
		else if (afterChannel != null) {
			this.afterChannelInit = afterChannel;
		}
		else if (builder.channelGroup != null) {
			this.afterChannelInit = builder.channelGroup::add;
		}
		else {
			this.afterChannelInit = null;
		}
	}
	
	/**
	 * 复制一份
	 * @return
	 */
	public ServerBootstrap get() {
		return bootstrapTemplate.clone();
	}

	/**
	 * 只是一个模板而已
	 * @return
	 */
	public ServerBootstrap getBootstrapTemplate() {
		return bootstrapTemplate;
	}

	public boolean isPreferNative() {
		return preferNative;
	}

	public SslContext getSslContext() {
		return sslContext;
	}

	public long getSslHandshakeTimeoutMillis() {
		return sslHandshakeTimeoutMillis;
	}

	public long getSslCloseNotifyFlushTimeoutMillis() {
		return sslCloseNotifyFlushTimeoutMillis;
	}

	public long getSslCloseNotifyReadTimeoutMillis() {
		return sslCloseNotifyReadTimeoutMillis;
	}

	public Consumer<? super Channel> getAfterChannelInit() {
		return afterChannelInit;
	}

	public Consumer<? super NettyContext> getAfterNettyContextInit() {
		return afterNettyContextInit;
	}

	public Predicate<? super Channel> getOnChannelInit() {
		return onChannelInit;
	}

	public static class Builder {

		private static final boolean DEFAULT_NATIVE;

		static {
			// reactor.ipc.netty.epoll should be deprecated in favor of
			// reactor.ipc.netty.native
			String defaultNativeEpoll = System.getProperty("reactor.ipc.netty.epoll");

			String defaultNative = System.getProperty("reactor.netty.native");

			if (defaultNative != null) {
				DEFAULT_NATIVE = Boolean.parseBoolean(defaultNative);
			} else if (defaultNativeEpoll != null) {
				DEFAULT_NATIVE = Boolean.parseBoolean(defaultNativeEpoll);
			} else {
				DEFAULT_NATIVE = true;
			}
		}

		protected ServerBootstrap bootstrapTemplate;
		private boolean preferNative = DEFAULT_NATIVE;
		private ChannelGroup channelGroup = null;
		private SslContext sslContext = null;
		private long sslHandshakeTimeoutMillis = 10000L;
		private long sslCloseNotifyFlushTimeoutMillis = 3000L;
		private long sslCloseNotifyReadTimeoutMillis = 0L;

		private Consumer<? super Channel> afterChannelInit = null;
		private Consumer<? super NettyContext> afterNettyContextInit = null;
		private Predicate<? super Channel> onChannelInit = null;
		
		protected Builder(ServerBootstrap bootstrapTemplate) {
			this.bootstrapTemplate = bootstrapTemplate;
			defaultNettyOptions(this.bootstrapTemplate);
		}
		
		private void defaultNettyOptions(AbstractBootstrap<?, ?> bootstrap) {
			bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		}
		
		/**
		 * Attribute default attribute to the future {@link Channel} connection. They will
		 * be available via {@link reactor.ipc.netty.NettyInbound#attr(AttributeKey)}.
		 *
		 * @param key the attribute key
		 * @param value the attribute value
		 * @param <T> the attribute type
		 * @return {@code this}
		 * @see Bootstrap#attr(AttributeKey, Object)
		 */
		public <T> Builder attr(AttributeKey<T> key, T value) {
			this.bootstrapTemplate.attr(key, value);
			return this;
		}

		/**
		 * Set a {@link ChannelOption} value for low level connection settings like
		 * SO_TIMEOUT or SO_KEEPALIVE. This will apply to each new channel from remote
		 * peer.
		 *
		 * @param key the option key
		 * @param value the option value
		 * @param <T> the option type
		 * @return {@code this}
		 * @see Bootstrap#option(ChannelOption, Object)
		 */
		public <T> Builder option(ChannelOption<T> key, T value) {
			this.bootstrapTemplate.option(key, value);
			return this;
		}
		
		/**
		 * Set the preferred native option. Determine if epoll/kqueue should be used if available.
		 *
		 * @param preferNative Should the connector prefer native (epoll/kqueue) if available
		 * @return {@code this}
		 */
		public final Builder preferNative(boolean preferNative) {
			this.preferNative = preferNative;
			return this;
		}
		
		/**
		 * Provide a {@link ChannelGroup} for each active remote channel will be held in the
		 * provided group.
		 *
		 * @param channelGroup a {@link ChannelGroup} to monitor remote channel
		 * @return {@code this}
		 */
		public final Builder channelGroup(ChannelGroup channelGroup) {
			this.channelGroup = Objects.requireNonNull(channelGroup, "channelGroup");
			return this;
		}
		
		/**
		 * Set the options to use for configuring SSL. Setting this to {@code null} means
		 * don't use SSL at all (the default).
		 *
		 * @param sslContext The context to set when configuring SSL
		 * @return {@code this}
		 */
		public final Builder sslContext(SslContext sslContext) {
			this.sslContext = sslContext;
			return this;
		}
		
		/**
		 * Set the options to use for configuring SSL handshake timeout. Default to 10000 ms.
		 *
		 * @param sslHandshakeTimeout The timeout {@link Duration}
		 * @return {@code this}
		 */
		public final Builder sslHandshakeTimeout(Duration sslHandshakeTimeout) {
			Objects.requireNonNull(sslHandshakeTimeout, "sslHandshakeTimeout");
			return sslHandshakeTimeoutMillis(sslHandshakeTimeout.toMillis());
		}

		/**
		 * Set the options to use for configuring SSL handshake timeout. Default to 10000 ms.
		 *
		 * @param sslHandshakeTimeoutMillis The timeout in milliseconds
		 * @return {@code this}
		 */
		public final Builder sslHandshakeTimeoutMillis(long sslHandshakeTimeoutMillis) {
			if(sslHandshakeTimeoutMillis < 0L){
				throw new IllegalArgumentException("ssl handshake timeout must be positive," +
						" was: "+sslHandshakeTimeoutMillis);
			}
			this.sslHandshakeTimeoutMillis = sslHandshakeTimeoutMillis;
			return this;
		}

		/**
		 * Set the options to use for configuring SSL close_notify flush timeout. Default to 3000 ms.
		 *
		 * @param sslCloseNotifyFlushTimeout The timeout {@link Duration}
		 *
		 * @return {@code this}
		 */
		public final Builder sslCloseNotifyFlushTimeout(Duration sslCloseNotifyFlushTimeout) {
			Objects.requireNonNull(sslCloseNotifyFlushTimeout, "sslCloseNotifyFlushTimeout");
			return sslCloseNotifyFlushTimeoutMillis(sslCloseNotifyFlushTimeout.toMillis());
		}


		/**
		 * Set the options to use for configuring SSL close_notify flush timeout. Default to 3000 ms.
		 *
		 * @param sslCloseNotifyFlushTimeoutMillis The timeout in milliseconds
		 *
		 * @return {@code this}
		 */
		public final Builder sslCloseNotifyFlushTimeoutMillis(long sslCloseNotifyFlushTimeoutMillis) {
			if (sslCloseNotifyFlushTimeoutMillis < 0L) {
				throw new IllegalArgumentException("ssl close_notify flush timeout must be positive," +
						" was: " + sslCloseNotifyFlushTimeoutMillis);
			}
			this.sslCloseNotifyFlushTimeoutMillis = sslCloseNotifyFlushTimeoutMillis;
			return this;
		}


		/**
		 * Set the options to use for configuring SSL close_notify read timeout. Default to 0 ms.
		 *
		 * @param sslCloseNotifyReadTimeout The timeout {@link Duration}
		 *
		 * @return {@code this}
		 */
		public final Builder sslCloseNotifyReadTimeout(Duration sslCloseNotifyReadTimeout) {
			Objects.requireNonNull(sslCloseNotifyReadTimeout, "sslCloseNotifyReadTimeout");
			return sslCloseNotifyFlushTimeoutMillis(sslCloseNotifyReadTimeout.toMillis());
		}


		/**
		 * Set the options to use for configuring SSL close_notify read timeout. Default to 0 ms.
		 *
		 * @param sslCloseNotifyReadTimeoutMillis The timeout in milliseconds
		 *
		 * @return {@code this}
		 */
		public final Builder sslCloseNotifyReadTimeoutMillis(long sslCloseNotifyReadTimeoutMillis) {
			if (sslCloseNotifyReadTimeoutMillis < 0L) {
				throw new IllegalArgumentException("ssl close_notify read timeout must be positive," +
						" was: " + sslCloseNotifyReadTimeoutMillis);
			}
			this.sslCloseNotifyReadTimeoutMillis = sslCloseNotifyReadTimeoutMillis;
			return this;
		}

		/**
		 * Setup a callback called after each {@link Channel} initialization, once
		 * reactor-netty pipeline handlers have been registered.
		 *
		 * @param afterChannelInit the post channel setup handler
		 * @return {@code this}
		 * @see #onChannelInit(Predicate)
		 * @see #afterNettyContextInit(Consumer)
		 */
		public final Builder afterChannelInit(Consumer<? super Channel> afterChannelInit) {
			this.afterChannelInit = Objects.requireNonNull(afterChannelInit, "afterChannelInit");
			return this;
		}

		/**
		 * Setup a {@link Predicate} for each {@link Channel} initialization that can be
		 * used to prevent the Channel's registration.
		 *
		 * @param onChannelInit predicate to accept or reject the newly created Channel
		 * @return {@code this}
		 * @see #afterChannelInit(Consumer)
		 * @see #afterNettyContextInit(Consumer)
		 */
		public final Builder onChannelInit(Predicate<? super Channel> onChannelInit) {
			this.onChannelInit = Objects.requireNonNull(onChannelInit, "onChannelInit");
			return this;
		}

		/**
		 * Setup a callback called after each {@link Channel} initialization, once the
		 * reactor-netty pipeline handlers have been registered and the {@link NettyContext}
		 * is available.
		 *
		 * @param afterNettyContextInit the post channel setup handler
		 * @return {@code this}
		 * @see #onChannelInit(Predicate)
		 * @see #afterChannelInit(Consumer)
		 */
		public final Builder afterNettyContextInit(Consumer<? super NettyContext> afterNettyContextInit) {
			this.afterNettyContextInit = Objects.requireNonNull(afterNettyContextInit, "afterNettyContextInit");
			return this;
		}
	}
}