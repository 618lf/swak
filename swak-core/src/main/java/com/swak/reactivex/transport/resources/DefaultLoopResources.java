package com.swak.reactivex.transport.resources;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.reactivex.threads.BlockedThreadChecker;
import com.swak.reactivex.threads.SwakThreadFactory;
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
 * @author: lifeng
 * @date: 2020/3/29 12:50
 */
public class DefaultLoopResources extends AtomicInteger implements LoopResources {

    private static final long serialVersionUID = 1L;

    final String prefix;
    final boolean daemon;
    final int selectCount;
    final int workerCount;
    final long maxExecTime;
    final TimeUnit maxExecTimeUnit;
    final BlockedThreadChecker blockedThreadChecker;
    EventLoopGroup serverLoops;
    EventLoopGroup serverSelectLoops;

    DefaultLoopResources(String prefix, int selectCount, int workerCount, boolean daemon,
                         BlockedThreadChecker blockedThreadChecker, long maxExecTime, TimeUnit maxExecTimeUnit) {
        super(1);
        this.daemon = daemon;
        this.workerCount = workerCount == -1 ? Math.max(Runtime.getRuntime().availableProcessors() * 2, 4)
                : workerCount;
        this.selectCount = selectCount == -1 ? this.workerCount : selectCount;
        this.prefix = prefix;
        this.blockedThreadChecker = blockedThreadChecker;
        this.maxExecTime = maxExecTime;
        this.maxExecTimeUnit = maxExecTimeUnit;
    }

    @Override
    public Class<? extends ServerChannel> onServerChannel() {
        return NioServerSocketChannel.class;
    }

    @Override
    public Class<? extends Channel> onClientChannel() {
        return NioSocketChannel.class;
    }

    @Override
    public EventLoopGroup onServerSelect() {
        if (serverSelectLoops == null) {
            this.serverSelectLoops = new NioEventLoopGroup(selectCount, threadFactory(this, "Acceptor-"));
        }
        return serverSelectLoops;
    }

    @Override
    public EventLoopGroup onServer() {
        if (this.serverLoops == null) {
            this.serverLoops = new NioEventLoopGroup(workerCount, threadFactory(this, "Eventloop-"));
        }
        return serverLoops;
    }

    @Override
    public EventLoopGroup onClient() {
        if (this.serverLoops == null) {
            this.serverLoops = new NioEventLoopGroup(workerCount, threadFactory(this, "Client-"));
        }
        return serverLoops;
    }

    @Override
    public int workCount() {
        return workerCount;
    }

    /**
     * 关闭资源
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Mono<Void> disposeLater() {
        if (serverSelectLoops != null && !serverSelectLoops.isShutdown()) {
            serverSelectLoops.shutdownGracefully();
        }
        if (serverLoops != null && !serverLoops.isShutdown()) {
            serverLoops.shutdownGracefully();
        }
        Mono<?> sslMono = serverSelectLoops != null ? FutureMono.from((Future) serverSelectLoops.terminationFuture())
                : Mono.empty();
        Mono<?> slMono = serverLoops != null ? FutureMono.from((Future) serverLoops.terminationFuture()) : Mono.empty();
        return Mono.when(sslMono, slMono);
    }

    /**
     * 线程管理器
     *
     * @param parent LoopResources
     * @param prefix 线程前缀
     * @return 线程管理器
     */
    ThreadFactory threadFactory(DefaultLoopResources parent, String prefix) {
        return new SwakThreadFactory(parent.prefix + prefix, parent.daemon, parent, parent.blockedThreadChecker,
                parent.maxExecTime, parent.maxExecTimeUnit);
    }
}
