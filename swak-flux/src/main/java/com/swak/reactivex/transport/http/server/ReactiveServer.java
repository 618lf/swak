package com.swak.reactivex.transport.http.server;

import java.util.function.BiFunction;

import org.springframework.boot.web.server.WebServerException;

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
	 * 服务器监听的端口
	 */
	@Override
	public String getAddresses() {
		return String.valueOf(realServer.getAddress().getPort());
	}

	/**
	 * 停止实际的服务器
	 */
	@Override
	public void stop() throws WebServerException {
		realServer.stop();
	}
}