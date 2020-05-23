package com.swak.flux.transport.server;

import java.util.function.BiFunction;

import org.springframework.boot.web.server.WebServerException;

import com.swak.reactivex.context.EndPoints;
import com.swak.reactivex.context.Server;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.tcp.TcpServer;

import reactor.core.publisher.Mono;

/**
 * 响应式的 http 服务器
 * 
 * @author lifeng
 */
public class ReactiveServer implements Server {

	private final TcpServer realServer;
	private final BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler;

	public ReactiveServer(TcpServer realServer,
			BiFunction<? extends NettyInbound, ? extends NettyOutbound, Mono<Void>> handler) {
		this.realServer = realServer;
		this.handler = handler;
	}

	/**
	 * 启动实际的服务器
	 */
	@Override
	public void start() throws WebServerException {
		realServer.start(handler);
	}

	/**
	 * 显示启动的服务
	 */
	@Override
	public EndPoints getEndPoints() {
		return null;
	}

	/**
	 * 停止实际的服务器
	 */
	@Override
	public void stop() throws WebServerException {
		realServer.stop();
	}
}