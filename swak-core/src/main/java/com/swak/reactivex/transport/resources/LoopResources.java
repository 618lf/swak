package com.swak.reactivex.transport.resources;

import java.util.concurrent.TimeUnit;

import com.swak.OS;
import com.swak.reactivex.threads.BlockedThreadChecker;
import com.swak.reactivex.transport.Disposable;
import com.swak.reactivex.transport.TransportMode;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import reactor.core.publisher.Mono;

/**
 * EventLoopGroup
 * 
 * @author lifeng
 */
public interface LoopResources extends Disposable {

	/**
	 * 创建
	 * 
	 * @param mode
	 * @param select
	 * @param worker
	 * @param prefix
	 * @param daemon
	 * @return
	 */
	static LoopResources create(TransportMode mode, String prefix, int select, int worker, boolean daemon,
			BlockedThreadChecker checker, long maxExecTime, TimeUnit maxExecTimeUnit) {
		if (mode != null && TransportMode.EPOLL == mode) {
			return new EpollLoopResources(prefix, select, worker, daemon, checker, maxExecTime, maxExecTimeUnit);
		}
		return new DefaultLoopResources(prefix, select, worker, daemon, checker, maxExecTime, maxExecTimeUnit);
	}

	/**
	 * 类型
	 * 
	 * @return
	 */
	static TransportMode transportModeFitOs() {
		if (OS.me() == OS.linux) {
			return TransportMode.EPOLL;
		}
		return TransportMode.NIO;
	}

	Class<? extends ServerChannel> onServerChannel();

	EventLoopGroup onServerSelect();

	EventLoopGroup onServer();

	Class<? extends Channel> onClientChannel();

	EventLoopGroup onClient();

	default void dispose() {
		disposeLater().subscribe();
	}

	default Mono<Void> disposeLater() {
		return Mono.empty();
	}
}