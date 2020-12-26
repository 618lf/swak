package com.swak.reactivex.transport.resources;

import java.util.concurrent.TimeUnit;

import com.swak.OS;
import com.swak.reactivex.threads.BlockedThreadChecker;
import com.swak.reactivex.transport.Disposable;
import com.swak.reactivex.transport.TransportMode;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.DatagramChannel;
import reactor.core.publisher.Mono;

/**
 * EventLoopGroup
 *
 * @author: lifeng
 * @date: 2020/3/29 12:51
 */
public interface LoopResources extends Disposable {

    /**
     * 创建执行io任务的线程池 -- Eventloop 暂时无法做监控
     *
     * @param mode            传输的模式
     * @param select          事件监听线程数
     * @param worker          事件处理线程数
     * @param prefix          线程前缀
     * @param daemon          是否守护线程
     * @param checker         线程检查
     * @param maxExecTime     最大执行时间
     * @param maxExecTimeUnit 最大执行时间类型
     * @return LoopResources
     */
    static LoopResources create(TransportMode mode, String prefix, int select, int worker, boolean daemon,
                                BlockedThreadChecker checker, long maxExecTime, TimeUnit maxExecTimeUnit) {
        if (TransportMode.EPOLL == mode) {
            return new EpollLoopResources(prefix, select, worker, daemon, checker, maxExecTime, maxExecTimeUnit);
        }
        return new DefaultLoopResources(prefix, select, worker, daemon, checker, maxExecTime, maxExecTimeUnit);
    }

    /**
     * 自动根据操作系统类型识别 传输模式
     *
     * @return 传输模式
     * @author lifeng
     * @date 2020/3/29 12:55
     */
    static TransportMode transportModeFitOs() {
        if (OS.me() == OS.linux) {
            return TransportMode.EPOLL;
        }
        return TransportMode.NIO;
    }

    /**
     * ServerChannel 实现类
     *
     * @return ServerChannel 实现类
     * @author lifeng
     * @date 2020/3/29 12:55
     */
    Class<? extends ServerChannel> onServerChannel();
    
    /**
     * DatagramChannel 实现类
     *
     * @return ServerChannel 实现类
     * @author lifeng
     * @date 2020/3/29 12:55
     */
    Class<? extends DatagramChannel> onDatagramChannel();

    /**
     * 事件监听线程池组
     *
     * @return 事件监听线程池组
     * @author lifeng
     * @date 2020/3/29 12:56
     */
    EventLoopGroup onServerSelect();

    /**
     * 事件处理线程池组
     *
     * @return 事件处理线程池组
     * @author lifeng
     * @date 2020/3/29 12:56
     */
    EventLoopGroup onServer();

    /**
     * ClientChannel 实现类
     *
     * @return ClientChannel 实现类
     * @author lifeng
     * @date 2020/3/29 12:56
     */
    Class<? extends Channel> onClientChannel();

    /**
     * 事件处理线程池组
     *
     * @return 事件处理线程池组
     * @author lifeng
     * @date 2020/3/29 12:56
     */
    EventLoopGroup onClient();

    /**
     * 事件处理线程池组大小
     *
     * @return 事件处理线程池组大小
     * @author lifeng
     * @date 2020/3/29 12:56
     */
    int workCount();

    /**
     * 关闭
     *
     * @author lifeng
     * @date 2020/3/29 12:58
     */
    @Override
    default void dispose() {
        disposeLater().subscribe();
    }

    /**
     * 异步关闭
     *
     * @author lifeng
     * @date 2020/3/29 12:58
     */
    default Mono<Void> disposeLater() {
        return Mono.empty();
    }
}