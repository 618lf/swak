package com.swak.reactivex.transport.channel;

import java.util.concurrent.TimeUnit;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.channel.Channel;
import io.netty.util.AsciiString;
import reactor.core.publisher.MonoSink;

/**
 * 代表服务端的连接
 *
 * @author: lifeng
 * @date: 2020/3/29 12:47
 */
public class ServerContextHandler extends CloseableContextHandler implements NettyContext {

    public static volatile CharSequence DATE = new AsciiString(GmtDateKit.format());

    protected final ServerOptions options;

    protected ServerContextHandler(ServerOptions serverOptions, MonoSink<NettyContext> sink) {
        super(serverOptions, sink);
        this.options = serverOptions;
    }

    /**
     * 服务器 Channel
     */
    @Override
    protected void doStarted(Channel channel) {

        // 激活 NettyContext
        this.fireContextActive(this);

        // 自动更新时间
        options.dateServer().scheduleWithFixedDelay(() -> DATE = new AsciiString(GmtDateKit.format()), 1000, 1000,
                TimeUnit.MILLISECONDS);
    }

    /**
     * 服务器 Channel
     */
    @Override
    public Channel channel() {
        return f.channel();
    }

    /**
     * 关闭 客户端连接
     */
    @Override
    public void terminateChannel(Channel channel) {
        if (!f.channel().isActive()) {
            return;
        }
        channel.close();
    }
}