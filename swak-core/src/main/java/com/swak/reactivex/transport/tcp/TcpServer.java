package com.swak.reactivex.transport.tcp;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import reactor.core.publisher.Mono;

/**
 * A TCP server connector.
 *
 * @author: lifeng
 * @date: 2020/3/29 12:59
 */
public abstract class TcpServer implements BiConsumer<ChannelPipeline, ContextHandler> {

    protected Logger logger = LoggerFactory.getLogger(TcpServer.class);

    /**
     * 配置 ServerOptions
     *
     * @return ServerOptions
     * @author lifeng
     * @date 2020/3/29 13:04
     */
    public abstract ServerOptions options();

    /**
     * 启动服务器
     *
     * @param handler 数据处理
     * @author lifeng
     * @date 2020/3/29 13:00
     */
    public abstract void start(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler);

    /**
     * 异步启动服务器，并注册启动监听
     *
     * @param ioHandler 处理器
     * @return 异步服务
     */
    @SuppressWarnings("unchecked")
    public Mono<? extends NettyContext> connector(BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> ioHandler) {
        return Mono.create(sink -> {

            // 配置项
            ServerOptions options = options();

            // init Handler
            ContextHandler contextHandler = ContextHandler.newServerContext(options, sink)
                    .onPipeline(this).onChannel((c, ch, request) -> this.doHandler(c, ch, request, (BiFunction<NettyInbound, NettyOutbound, Mono<Void>>) ioHandler));

            // start server
            ServerBootstrap b = options.get()
                    .localAddress(options.getAddress())
                    .childHandler(contextHandler);

            // 监听启动过程
            contextHandler.setFuture(b.bind());
        });
    }

    /**
     * handler 处理器
     *
     * @param c              连接
     * @param contextHandler 服务处理
     * @param msg            数据
     * @param ioHandler      数据处理
     * @return 处理器
     * @author lifeng
     * @date 2020/3/29 13:02
     */
    public abstract ChannelOperations<?, ?> doHandler(Channel c, ContextHandler contextHandler, Object msg, BiFunction<NettyInbound, NettyOutbound, Mono<Void>> ioHandler);

    /**
     * 协议
     *
     * @return 协议
     */
    public abstract String getProtocol();

    /**
     * 服务器的地址
     *
     * @return 地址
     */
    public abstract InetSocketAddress getAddress();

    /**
     * 停止服务器
     */
    public abstract void stop();
}