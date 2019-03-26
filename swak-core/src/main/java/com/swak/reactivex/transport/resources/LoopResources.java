package com.swak.reactivex.transport.resources;

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
	 * Flux 中设置为了后台进行，因为单独有一个后台进行等待关闭
	 * 
	 * @param mode
	 * @param select
	 * @param worker
	 * @param prefix
	 * @return
	 */
	static LoopResources create(TransportMode mode, Integer select, Integer worker, String prefix) {
		return LoopResources.create(mode, select, worker, prefix, true);
	}
	
	/**
	 * 其他模块不建议使用后台进行，同时需要提供关闭的入口
	 * 
	 * @param mode
	 * @param select
	 * @param worker
	 * @param prefix
	 * @param daemon
	 * @return
	 */
	static LoopResources create(TransportMode mode, Integer select, Integer worker, String prefix, boolean daemon) {
		if (mode != null && TransportMode.EPOLL == mode) {
			return new DefaultEpollLoopResources(prefix, select, worker, daemon);
		}
		return new DefaultLoopResources(prefix, select, worker, daemon);
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