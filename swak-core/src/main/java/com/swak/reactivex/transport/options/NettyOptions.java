package com.swak.reactivex.transport.options;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.resources.LoopResources;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;

/**
 * NettyOptions
 *
 * @author: lifeng
 * @date: 2020/3/29 12:48
 */
public class NettyOptions<BOOTSTRAP extends AbstractBootstrap<BOOTSTRAP, ?>> {

    private final BOOTSTRAP bootstrapTemplate;
    private final SslContext sslContext;
    private final long sslHandshakeTimeoutMillis;
    private final long sslCloseNotifyFlushTimeoutMillis;
    private final long sslCloseNotifyReadTimeoutMillis;
    private final long readTimeoutMillis;
    private final long writeTimeoutMillis;
    protected final Consumer<? super Channel> afterChannelInit;
    protected final Consumer<? super NettyContext> afterNettyContextInit;
    private final Predicate<? super Channel> onChannelInit;
    private final LoopResources loopResources;

    protected NettyOptions(NettyOptions.Builder<BOOTSTRAP> builder) {
        this.bootstrapTemplate = builder.bootstrapTemplate;
        this.sslContext = builder.sslContext;
        this.sslHandshakeTimeoutMillis = builder.sslHandshakeTimeoutMillis;
        this.sslCloseNotifyFlushTimeoutMillis = builder.sslCloseNotifyFlushTimeoutMillis;
        this.sslCloseNotifyReadTimeoutMillis = builder.sslCloseNotifyReadTimeoutMillis;
        this.afterNettyContextInit = builder.afterNettyContextInit;
        this.onChannelInit = builder.onChannelInit;
        this.loopResources = builder.loopResources;
        this.readTimeoutMillis = builder.readTimeoutMillis;
        this.writeTimeoutMillis = builder.writeTimeoutMillis;

        Consumer<? super Channel> afterChannel = builder.afterChannelInit;
        if (afterChannel != null && builder.channelGroup != null) {
            this.afterChannelInit = ((Consumer<Channel>) builder.channelGroup::add).andThen(afterChannel);
        } else if (afterChannel != null) {
            this.afterChannelInit = afterChannel;
        } else if (builder.channelGroup != null) {
            this.afterChannelInit = builder.channelGroup::add;
        } else {
            this.afterChannelInit = null;
        }
    }

    /**
     * 复制一份
     */
    public BOOTSTRAP get() {
        return bootstrapTemplate.clone();
    }

    /**
     * 只是一个模板而已
     */
    public BOOTSTRAP getBootstrapTemplate() {
        return bootstrapTemplate;
    }

    public LoopResources getLoopResources() {
        return loopResources;
    }

    public SslContext sslContext() {
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

    public long getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public long getWriteTimeoutMillis() {
        return writeTimeoutMillis;
    }

    public Consumer<? super Channel> afterChannelInit() {
        return afterChannelInit;
    }

    public Consumer<? super NettyContext> afterNettyContextInit() {
        return afterNettyContextInit;
    }

    public Predicate<? super Channel> onChannelInit() {
        return onChannelInit;
    }

    public SslHandler getSslHandler(ByteBufAllocator allocator) {
        SslContext sslContext = this.sslContext;

        if (sslContext == null) {
            return null;
        }

        Objects.requireNonNull(allocator, "allocator");
        SslHandler sslHandler = sslContext.newHandler(allocator);
        sslHandler.setHandshakeTimeoutMillis(sslHandshakeTimeoutMillis);
        sslHandler.setCloseNotifyFlushTimeoutMillis(sslCloseNotifyFlushTimeoutMillis);
        sslHandler.setCloseNotifyReadTimeoutMillis(sslCloseNotifyReadTimeoutMillis);
        return sslHandler;
    }

    public static class Builder<BOOTSTRAP extends AbstractBootstrap<BOOTSTRAP, ?>> {

        protected BOOTSTRAP bootstrapTemplate;
        private ChannelGroup channelGroup = null;
        private SslContext sslContext = null;
        private long sslHandshakeTimeoutMillis = 10000L;
        private long sslCloseNotifyFlushTimeoutMillis = 3000L;
        private long sslCloseNotifyReadTimeoutMillis = 0L;
        private long readTimeoutMillis = -1L;
        private long writeTimeoutMillis = -1L;
        private LoopResources loopResources;

        private Consumer<? super Channel> afterChannelInit = null;
        private Consumer<? super NettyContext> afterNettyContextInit = null;
        private Predicate<? super Channel> onChannelInit = null;

        protected Builder(BOOTSTRAP bootstrapTemplate) {
            this.bootstrapTemplate = bootstrapTemplate;
            defaultNettyOptions(this.bootstrapTemplate);
        }

        private void defaultNettyOptions(AbstractBootstrap<?, ?> bootstrap) {
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        public <T> Builder<BOOTSTRAP> attr(AttributeKey<T> key, T value) {
            this.bootstrapTemplate.attr(key, value);
            return this;
        }

        public <T> Builder<BOOTSTRAP> option(ChannelOption<T> key, T value) {
            this.bootstrapTemplate.option(key, value);
            return this;
        }

        public final Builder<BOOTSTRAP> channelGroup(ChannelGroup channelGroup) {
            this.channelGroup = Objects.requireNonNull(channelGroup, "channelGroup");
            return this;
        }

        public final Builder<BOOTSTRAP> sslContext(SslContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        public final Builder<BOOTSTRAP> loopResources(LoopResources loopResources) {
            this.loopResources = Objects.requireNonNull(loopResources, "loopResources");
            return this;
        }

        public final Builder<BOOTSTRAP> sslHandshakeTimeout(Duration sslHandshakeTimeout) {
            Objects.requireNonNull(sslHandshakeTimeout, "sslHandshakeTimeout");
            return sslHandshakeTimeoutMillis(sslHandshakeTimeout.toMillis());
        }

        public final Builder<BOOTSTRAP> sslHandshakeTimeoutMillis(long sslHandshakeTimeoutMillis) {
            if (sslHandshakeTimeoutMillis < 0L) {
                throw new IllegalArgumentException(
                        "ssl handshake timeout must be positive," + " was: " + sslHandshakeTimeoutMillis);
            }
            this.sslHandshakeTimeoutMillis = sslHandshakeTimeoutMillis;
            return this;
        }

        public final Builder<BOOTSTRAP> sslCloseNotifyFlushTimeout(Duration sslCloseNotifyFlushTimeout) {
            Objects.requireNonNull(sslCloseNotifyFlushTimeout, "sslCloseNotifyFlushTimeout");
            return sslCloseNotifyFlushTimeoutMillis(sslCloseNotifyFlushTimeout.toMillis());
        }

        public final Builder<BOOTSTRAP> sslCloseNotifyFlushTimeoutMillis(long sslCloseNotifyFlushTimeoutMillis) {
            if (sslCloseNotifyFlushTimeoutMillis < 0L) {
                throw new IllegalArgumentException("ssl close_notify flush timeout must be positive," + " was: "
                        + sslCloseNotifyFlushTimeoutMillis);
            }
            this.sslCloseNotifyFlushTimeoutMillis = sslCloseNotifyFlushTimeoutMillis;
            return this;
        }

        public final Builder<BOOTSTRAP> sslCloseNotifyReadTimeout(Duration sslCloseNotifyReadTimeout) {
            Objects.requireNonNull(sslCloseNotifyReadTimeout, "sslCloseNotifyReadTimeout");
            return sslCloseNotifyFlushTimeoutMillis(sslCloseNotifyReadTimeout.toMillis());
        }

        public final Builder<BOOTSTRAP> sslCloseNotifyReadTimeoutMillis(long sslCloseNotifyReadTimeoutMillis) {
            if (sslCloseNotifyReadTimeoutMillis < 0L) {
                throw new IllegalArgumentException(
                        "ssl close_notify read timeout must be positive," + " was: " + sslCloseNotifyReadTimeoutMillis);
            }
            this.sslCloseNotifyReadTimeoutMillis = sslCloseNotifyReadTimeoutMillis;
            return this;
        }

        public final Builder<BOOTSTRAP> readTimeoutMillis(long readTimeoutMillis) {
            Objects.requireNonNull(readTimeoutMillis, "readTimeoutMillis");
            this.readTimeoutMillis = readTimeoutMillis;
            return this;
        }

        public final Builder<BOOTSTRAP> writeTimeoutMillis(long writeTimeoutMillis) {
            Objects.requireNonNull(writeTimeoutMillis, "writeTimeoutMillis");
            this.writeTimeoutMillis = writeTimeoutMillis;
            return this;
        }

        public final Builder<BOOTSTRAP> afterChannelInit(Consumer<? super Channel> afterChannelInit) {
            this.afterChannelInit = Objects.requireNonNull(afterChannelInit, "afterChannelInit");
            return this;
        }

        public final Builder<BOOTSTRAP> onChannelInit(Predicate<? super Channel> onChannelInit) {
            this.onChannelInit = Objects.requireNonNull(onChannelInit, "onChannelInit");
            return this;
        }

        public final Builder<BOOTSTRAP> afterNettyContextInit(Consumer<? super NettyContext> afterNettyContextInit) {
            this.afterNettyContextInit = Objects.requireNonNull(afterNettyContextInit, "afterNettyContextInit");
            return this;
        }
    }
}