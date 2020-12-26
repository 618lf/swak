package com.swak.reactivex.transport.resources;

import java.util.concurrent.TimeUnit;

import com.swak.reactivex.threads.BlockedThreadChecker;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.DatagramChannel;

/**
 * EpollLoop
 *
 * @author: lifeng
 * @date: 2020/3/29 12:51
 */
public class EpollLoopResources extends DefaultLoopResources {

    private static final long serialVersionUID = 1L;

    EpollLoopResources(String prefix, int selectCount, int workerCount, boolean daemon,
                       BlockedThreadChecker blockedThreadChecker, long maxExecTime, TimeUnit maxExecTimeUnit) {
        super(prefix, selectCount, workerCount, daemon, blockedThreadChecker, maxExecTime, maxExecTimeUnit);
    }
    
    @Override
    public Class<? extends DatagramChannel> onDatagramChannel() {
        return EpollDatagramChannel.class;
    }
    
    @Override
    public Class<? extends ServerChannel> onServerChannel() {
        return EpollServerSocketChannel.class;
    }

    @Override
    public Class<? extends Channel> onClientChannel() {
        return EpollSocketChannel.class;
    }

    @Override
    public EventLoopGroup onServerSelect() {
        if (this.serverSelectLoops == null) {
            this.serverSelectLoops = new EpollEventLoopGroup(selectCount, threadFactory(this, "Epoll-acceptor-"));
        }
        return this.serverSelectLoops;
    }

    @Override
    public EventLoopGroup onServer() {
        if (this.serverLoops == null) {
            this.serverLoops = new EpollEventLoopGroup(workerCount, threadFactory(this, "Epoll-eventloop-"));
        }
        return this.serverLoops;
    }

    @Override
    public EventLoopGroup onClient() {
        if (this.serverLoops == null) {
            this.serverLoops = new EpollEventLoopGroup(workerCount, threadFactory(this, "Epoll-client-"));
        }
        return this.serverLoops;
    }
}