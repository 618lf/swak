package com.swak.http.server;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AsciiString;

/**
 * 设置处理逻辑 这里支持http 协议即可
 * 
 * @author lifeng
 */
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	private HttpServerContext context;
	private SslContext sslCtx;
	private final boolean enableGzip;
	private final boolean enableCors;
	private final ScheduledExecutorService service;
	private final Executor executor = Executors.newFixedThreadPool(1024);

	private static final DateTimeFormatter GMT_FMT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz",
			Locale.US);
	public static volatile CharSequence date = new AsciiString(
			GMT_FMT.format(LocalDateTime.now().atZone(ZoneId.of("GMT"))));

	public HttpServerChannelInitializer(HttpServerContext context, ScheduledExecutorService service) {
		if (context.getBuilder().isSslOn()) {
			try {
				sslCtx = SslContextBuilder.forServer(new File(context.getBuilder().getCertFilePath()),
						new File(context.getBuilder().getPrivateKeyPath()), context.getBuilder().getPrivateKeyPassword()).build();
			} catch (SSLException e) {}
		}
		this.context = context;
		this.service = service;
		this.enableGzip = context.getBuilder().isEnableGzip();
		this.enableCors = context.getBuilder().isEnableCors();
		
		// 自动更新时间
		this.service.scheduleWithFixedDelay(
				() -> date = new AsciiString(GMT_FMT.format(LocalDateTime.now().atZone(ZoneId.of("GMT")))), 1000, 1000,
				TimeUnit.MILLISECONDS);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		if (sslCtx != null) {
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}
		if (enableGzip) {
			pipeline.addLast(new HttpContentCompressor());
		}
		pipeline.addLast(new HttpServerCodec(36192 * 2, 36192 * 8, 36192 * 16, false));
		pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
		pipeline.addLast(new ChunkedWriteHandler());
		if (enableCors) {
			CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
			pipeline.addLast(new CorsHandler(corsConfig));
		}
		pipeline.addLast("server", new HttpServerHandler(context, executor));
	}
}