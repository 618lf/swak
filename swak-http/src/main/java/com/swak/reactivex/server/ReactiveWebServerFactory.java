package com.swak.reactivex.server;

import java.io.File;

import org.springframework.boot.web.server.WebServer;

import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.server.options.HttpServerOptions;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * 还有一些属性每配置，在研究
 * @author lifeng
 */
public class ReactiveWebServerFactory {

	private HttpServerProperties properties;
	
	public ReactiveWebServerFactory(HttpServerProperties properties) {
		this.properties = properties;
	}
	
	public WebServer getWebServer(HttpHandler handler) {
		return new HttpServer(properties, handler);
	}
	
	/**
	 * 响应式的 http 服务器
	 * @return
	 */
	public ReactiveHttpServer createHttpServer() {
		return ReactiveHttpServer.options((options) -> {
			options.host(properties.getHost()).port(properties.getPort());
			if (properties.isSslOn()) {
				this.customizeSsl(options);
			}
			options.option(ChannelOption.TCP_NODELAY, properties.isTcpNoDelay());
			options.option(ChannelOption.SO_KEEPALIVE, properties.isSoKeepAlive());
			options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout());
		});
	}
	
	/**
	 * 配置ssl 
	 * @param builder
	 */
	public void customizeSsl(HttpServerOptions.Builder options) {
		try {
			SslContext sslCtx = SslContextBuilder.forServer(new File(properties.getCertFilePath()),
					new File(properties.getPrivateKeyPath()), properties.getPrivateKeyPassword()).build();
			options.sslContext(sslCtx);
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}