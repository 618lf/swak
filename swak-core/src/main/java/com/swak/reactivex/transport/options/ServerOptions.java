package com.swak.reactivex.transport.options;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.Consumer;

import com.swak.reactivex.transport.resources.LoopResources;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.AttributeKey;

/**
 * 服务器
 *
 * @author: lifeng
 * @date: 2020/3/29 12:49
 */
public class ServerOptions extends NettyOptions<ServerBootstrap> {

    private final InetSocketAddress localAddress;
    private EventLoop dateServer;
    private LogLevel logLevel;

    protected ServerOptions(ServerOptions.Builder builder) {
        super(builder);
        if (Objects.isNull(builder.host)) {
            this.localAddress = new InetSocketAddress(builder.port);
        } else {
            this.localAddress = new InetSocketAddress(builder.host, builder.port);
        }
        this.logLevel = builder.logLevel;
    }

    public final InetSocketAddress getAddress() {
        return localAddress;
    }

    public final EventLoop dateServer() {
        return dateServer;
    }

    public final LogLevel getLogLevel() {
        return logLevel;
    }

    /**
     * 复制一份
     */
    @Override
    public ServerBootstrap get() {
        ServerBootstrap b = super.get();
        serverLoghandler(b);
        groupAndChannel(b);
        return b;
    }

    final void serverLoghandler(ServerBootstrap bootstrap) {
        if (this.getLogLevel() != null) {
            bootstrap.handler(new LoggingHandler(this.getLogLevel()));
        }
    }

    final void groupAndChannel(ServerBootstrap bootstrap) {
        LoopResources loops = this.getLoopResources();
        EventLoopGroup selectorGroup = loops.onServerSelect();
        EventLoopGroup elg = loops.onServer();
        bootstrap.group(selectorGroup, elg).channel(loops.onServerChannel());

        // 开启 日期服务
        dateServer = selectorGroup.next();
    }

    public static class Builder extends NettyOptions.Builder<ServerBootstrap> {

        private String host;
        private int port;
        private LogLevel logLevel;

        protected Builder(ServerBootstrap bootstrapTemplate) {
            super(bootstrapTemplate);
            this.defaultServerOptions(bootstrapTemplate);
        }

        private void defaultServerOptions(ServerBootstrap bootstrap) {
            bootstrap.option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_BACKLOG, 1000)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_RCVBUF, 1024 * 1024).childOption(ChannelOption.SO_SNDBUF, 1024 * 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
        }

        public final Builder host(String host) {
            this.host = host;
            return this;
        }

        public final Builder port(Integer port) {
            this.port = Objects.requireNonNull(port, "port");
            return this;
        }

        public final Builder logLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public final Builder sslSelfSigned() {
            return sslSelfSigned(c -> {
            });
        }

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

        public final <T> Builder childAttr(AttributeKey<T> key, T value) {
            this.bootstrapTemplate.childAttr(key, value);
            return this;
        }

        public final <T> Builder childOption(ChannelOption<T> key, T value) {
            this.bootstrapTemplate.childOption(key, value);
            return this;
        }
    }
}
