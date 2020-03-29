package com.swak.reactivex.transport.channel;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.NettyPipeline;
import com.swak.reactivex.transport.options.NettyOptions;
import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import reactor.core.publisher.MonoSink;

/**
 * 定义 Channel 的生命周期接口
 *
 * @author: lifeng
 * @date: 2020/3/29 12:44
 */
public abstract class ContextHandler extends ChannelInitializer<Channel> {

    static final Logger log = LoggerFactory.getLogger(ContextHandler.class);
    protected final NettyOptions<?> options;
    protected final MonoSink<NettyContext> sink;
    protected ChannelOperations.OnNew channelOpFactory;
    protected BiConsumer<ChannelPipeline, ContextHandler> pipelineConfigurator;
    protected volatile boolean fire;

    protected ContextHandler(NettyOptions<?> options, MonoSink<NettyContext> sink) {
        this.options = options;
        this.sink = sink;
    }

    //------------------ 配置 --------------------

    /**
     * 如何配置通道 -- accept
     */
    public final ContextHandler onPipeline(BiConsumer<ChannelPipeline, ContextHandler> pipelineConfigurator) {
        this.pipelineConfigurator = Objects.requireNonNull(pipelineConfigurator, "pipelineConfigurator");
        return this;
    }

    /**
     * 如何处理请求 -- doChannel
     */
    public final ContextHandler onChannel(ChannelOperations.OnNew channelOpFactory) {
        this.channelOpFactory = Objects.requireNonNull(channelOpFactory, "channelOpFactory");
        return this;
    }

    //------------------ 接入服务相关 (连接)--------------------

    /**
     * 启动服务
     *
     * @param future 异步结果
     * @author lifeng
     * @date 2020/3/29 12:46
     */
    public abstract void setFuture(Future<?> future);

    /**
     * 启动服务
     */
    protected void doStarted(Channel channel) {
    }

    /**
     * 停止服务
     */
    protected void doDropped(Channel channel) {
    }

    /**
     * 激活 NettyContext
     * 有两个类实现了NettyContext
     * 服务器: ServerContextHandler ， 服务器启动成功就激活
     * 客户端: HttpClientOperations ， 收到数据后才激活
     */
    public void fireContextActive(NettyContext context) {
        if (!fire) {
            fire = true;
            sink.success(context);
        }
    }

    /**
     * 发送错误处理，和 NettyContext 对应
     * 服务器: ServerContextHandler， 启动错误则发送错误
     * 客户端: HttpClientOperations， 连接或发送数据过程中出错则发送错误
     */
    public void fireContextError(Throwable t) {
        if (!fire) {
            fire = true;
            sink.error(t);
        } else {
            log.error("Connection closed remotely", t);
        }
    }

    //------------------ 初始化通道（初始化） --------------------

    /**
     * 获得配置
     */
    public final NettyOptions<?> options() {
        return this.options;
    }

    /**
     * 初始化通道
     */
    @Override
    protected void initChannel(Channel ch) {
        accept(ch);
    }

    /**
     * 配置通道
     */
    protected void accept(Channel channel) {
        try {
            doPipeline(channel);
            if (options.onChannelInit() != null) {
                if (options.onChannelInit().test(channel)) {
                    if (log.isDebugEnabled()) {
                        log.debug("DROPPED by onChannelInit predicate {}", channel);
                    }
                    doDropped(channel);
                    return;
                }
            }
            if (pipelineConfigurator != null) {
                pipelineConfigurator.accept(channel.pipeline(), this);
            }
        } finally {
            if (null != options.afterChannelInit()) {
                options.afterChannelInit().accept(channel);
            }
        }
    }

    /**
     * 通用配置
     */
    protected void doPipeline(Channel ch) {
        this.addSslHandler(ch.pipeline());
    }

    /**
     * 添加 SSL
     */
    protected void addSslHandler(ChannelPipeline pipeline) {
        SslHandler sslHandler = options.getSslHandler(pipeline.channel().alloc());
        if (sslHandler != null) {
            pipeline.addFirst(NettyPipeline.SslHandler, sslHandler);
        }
    }

    //------------------ 执行处理 （执行：响应请求，发送请求）--------------------

    /**
     * 实际的执行代码
     * 一般一个处理器同时只能处理一个请求，即使keepalive（http 请求来说）
     */
    @SuppressWarnings("unchecked")
    public <T extends ChannelOperations<?, ?>> T doChannel(Channel channel, Object request) {
        return (T) channelOpFactory.create(channel, this, request);
    }

    /**
     * 服务器： 关闭 inactive或出现异常的通道
     */
    public void terminateChannel(Channel channel) {
    }

    //------------------ 创建客户端 或 服务器 --------------------

    /**
     * 服务器
     */
    public static ContextHandler newServerContext(ServerOptions options, MonoSink<NettyContext> sink) {
        return new ServerContextHandler(options, sink);
    }
}