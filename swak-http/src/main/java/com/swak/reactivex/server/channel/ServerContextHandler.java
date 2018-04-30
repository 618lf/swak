package com.swak.reactivex.server.channel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.swak.reactivex.server.NettyContext;
import com.swak.reactivex.server.options.ServerOptions;
import com.swak.reactivex.server.tcp.TcpServer.Sink;

import io.netty.channel.Channel;
import io.netty.util.AsciiString;

/**
 * 配置服务器Channel
 * 
 * @author lifeng
 */
public class ServerContextHandler extends CloseableContextHandler implements NettyContext {

	private static final DateTimeFormatter GMT_FMT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz",
			Locale.US);
	public static volatile CharSequence date = new AsciiString(
			GMT_FMT.format(LocalDateTime.now().atZone(ZoneId.of("GMT"))));

	final ServerOptions options;

	ServerContextHandler(ServerOptions serverOptions, Sink<NettyContext> sink) {
		super(serverOptions, sink);
		this.options = serverOptions;
	}

	/**
	 * 服务器 Channel
	 */
	@Override
	protected void doStarted(Channel channel) {
		
		sink.success(this);

		// 自动更新时间
		options.dateServer().scheduleWithFixedDelay(
				() -> date = new AsciiString(GMT_FMT.format(LocalDateTime.now().atZone(ZoneId.of("GMT")))), 1000, 1000,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * 服务器 Channel
	 */
	@Override
	public Channel channel() {
		return f.channel();
	}
}
