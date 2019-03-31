package com.swak.flux.transport.http.server;

import java.util.function.BiFunction;

import org.springframework.boot.web.server.WebServerException;

import com.swak.Constants;
import com.swak.OS;
import com.swak.reactivex.context.Server;
import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.tcp.TcpServer;
import com.swak.utils.StringUtils;

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
		StringBuilder address = new StringBuilder(realServer.getProtocol());
		address.append("://").append("%s");
		int port = realServer.getAddress().getPort();
		if (port != 80) {
			address.append(":").append(port);
		}
		String hostName = realServer.getAddress().getHostString();
		if (!Constants.LOCALHOST.equals(hostName)) {
			hostName = OS.ip();
		}
		return StringUtils.format(address.toString(), hostName);
	}

	/**
	 * 停止实际的服务器
	 */
	@Override
	public void stop() throws WebServerException {
		realServer.stop();
	}
}