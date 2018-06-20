package com.swak.reactivex.transport.channel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.channel.Channel;
import io.netty.util.AsciiString;
import reactor.core.publisher.MonoSink;

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

	protected final ServerOptions options;

	protected ServerContextHandler(ServerOptions serverOptions, MonoSink<NettyContext> sink) {
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
	
	/**
	 * 关闭 客户端连接
	 */
	@Override
	public void terminateChannel(Channel channel) {
		if (!f.channel()
		     .isActive()) {
			return;
		}
		channel.close();
	}
}