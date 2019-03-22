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
	static LoopResources create(TransportMode mode, Integer select, Integer worker, String prefix) {
		if (mode != null && TransportMode.EPOLL == mode) {
			return new DefaultEpollLoopResources(prefix, select, worker, true);
		}
		return new DefaultLoopResources(prefix, select, worker, true);
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