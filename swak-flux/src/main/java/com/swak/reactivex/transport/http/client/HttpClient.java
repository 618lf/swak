package com.swak.reactivex.transport.http.client;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.NettyOutbound;
import com.swak.reactivex.transport.NettyPipeline;
import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.resources.LoopResources;
import com.swak.reactivex.transport.tcp.TcpClient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpMethod;
import reactor.core.publisher.Mono;

/**
 * http client
 * @author lifeng
 */
public class HttpClient extends TcpClient {

	static final HttpMethod WS = new HttpMethod("WS");
	final static String WS_SCHEME = "ws";
	final static String WSS_SCHEME = "wss";
	final static String HTTP_SCHEME = "http";
	final static String HTTPS_SCHEME = "https";
	private final HttpClientProperties properties;
	private LoopResources loopResources;

	public HttpClient(HttpClientProperties properties) {
		this.properties = properties;
		this.loopResources = LoopResources.create(properties.getMode(), properties.getServerWorker(),
				properties.getServerWorker(), properties.getName());
	}

	// ---------------------- 配置客户端 ---------------------
	@Override
	public HttpClientOptions options() {
		return this.options((options) -> {
			properties.getMode();
			options.loopResources(loopResources);
		});
	}

	/**
	 * 配置options
	 */
	private HttpClientOptions options(Consumer<? super HttpClientOptions.Builder> options) {
		HttpClientOptions.Builder serverOptionsBuilder = HttpClientOptions.builder();
		options.accept(serverOptionsBuilder);
		return serverOptionsBuilder.build();
	}

	// ---------------------- 初始化管道 -- 处理数据 ---------------------
	@Override
	public void accept(ChannelPipeline pipeline, ContextHandler u) {
		HttpClientOptions options = (HttpClientOptions)u.options();
		pipeline.addLast(NettyPipeline.HttpCodec, new HttpClientCodec());
		if (options.acceptGzip()) {
			pipeline.addAfter(NettyPipeline.HttpCodec, NettyPipeline.HttpDecompressor, new HttpContentDecompressor());
		}
	}
	
	@Override
	public ChannelOperations<?, ?> doHandler(Channel c, ContextHandler contextHandler, Object msg,
			BiFunction<NettyInbound, NettyOutbound, Mono<Void>> ioHandler) {
		return HttpClientOperations.bind(c, ioHandler, contextHandler);
	}

	/**
	 * 关闭客户端
	 */
	public void close() {
		this.loopResources.dispose();
	}
}