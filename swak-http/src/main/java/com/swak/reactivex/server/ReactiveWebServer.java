package com.swak.reactivex.server;

import java.util.function.Consumer;

import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.server.channel.ContextHandler;
import com.swak.reactivex.server.options.HttpServerOptions;
import com.swak.reactivex.server.tcp.BlockingNettyContext;
import com.swak.reactivex.server.tcp.TcpServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 响应式的 http 服务器
 * @author lifeng
 */
public class ReactiveWebServer extends TcpServer {

	private final HttpServerOptions options;
	private HttpHandler handler; 
	
	private ReactiveWebServer(Consumer<? super HttpServerOptions.Builder> options) {
		HttpServerOptions.Builder serverOptionsBuilder = HttpServerOptions.builder();
		options.accept(serverOptionsBuilder);
		this.options = serverOptionsBuilder.build();
	}
	
	public HttpServerOptions getOptions() {
		return options;
	}
	
	/**
	 * 启动服务器
	 * @param handler
	 * @return
	 */
	public BlockingNettyContext start(HttpHandler handler) {
		this.handler = handler;
		return new BlockingNettyContext(this.asyncStart(), "httpServer");
	}
	
	/**
	 * 创建 http 服务器
	 * @param options
	 * @return
	 */
	public static ReactiveWebServer options(Consumer<? super HttpServerOptions.Builder> options) {
		return new ReactiveWebServer(options);
	}

	/**
	 * 管道初始化配置
	 */
	@Override
	public void accept(ChannelPipeline p, ContextHandler u) {
		if (options.enabledCompression()) {
			p.addLast(NettyPipeline.HttpCompressor, new HttpContentCompressor());
		}
		p.addLast(NettyPipeline.HttpCodec,
				new HttpServerCodec(options.httpCodecMaxInitialLineLength(), options.httpCodecMaxHeaderSize(),
						options.httpCodecMaxChunkSize(), options.httpCodecValidateHeaders(),
						options.httpCodecInitialBufferSize()));
		p.addLast(NettyPipeline.HttpAggregator, new HttpObjectAggregator(Integer.MAX_VALUE));
		p.addLast(NettyPipeline.ChunkedWriter, new ChunkedWriteHandler());
		p.addLast(NettyPipeline.HttpServerHandler, new HttpServerHandler(u));
	}

	/**
	 * 管道数据处理
	 */
	@Override
	public void handleChannel(Channel channel, Object request) {
		HttpServerOperations op = HttpServerOperations.apply(handler).channel(channel).request((FullHttpRequest)request);
		channel.eventLoop().execute(op::handleStart);
	}
}