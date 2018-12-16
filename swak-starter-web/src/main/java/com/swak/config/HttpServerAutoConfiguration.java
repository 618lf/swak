package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.flux.WebHandlerAutoConfiguration;
import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.transport.http.server.HttpServer;
import com.swak.reactivex.transport.http.server.HttpServerProperties;
import com.swak.reactivex.transport.http.server.ReactiveServer;

import io.netty.handler.codec.http.multipart.DiskFileUpload;

/**
 * 服务器配置
 * 
 * @author lifeng
 */
@Configuration
@EnableConfigurationProperties(HttpServerProperties.class)
@AutoConfigureAfter({ WebHandlerAutoConfiguration.class })
public class HttpServerAutoConfiguration {

	private HttpServerProperties properties;

	public HttpServerAutoConfiguration(HttpServerProperties properties) {
		this.properties = properties;
		APP_LOGGER.debug("Loading Http Server");
	}

	/**
	 * -Dio.netty.leakDetectionLevel=PARANOID
	 * 构建 Reactive Server ，需要提供 HttpHandler 来处理 http 请求
	 * @param handler
	 * @return
	 */
	@Bean
	public ReactiveServer reactiveServer(HttpHandler handler) {
		// threadCache
		if (!properties.isThreadCache()) {
			System.setProperty("io.netty.allocator.tinyCacheSize", "0");
			System.setProperty("io.netty.allocator.smallCacheSize", "0");
			System.setProperty("io.netty.allocator.normalCacheSize", "0");
		}
		// leakDetection
		if (properties.getLeakDetectionLevel() != null) {
			System.setProperty("io.netty.leakDetection.level", properties.getLeakDetectionLevel().name());
		}
		// upload set
		DiskFileUpload.deleteOnExitTemporaryFile = false;
		DiskFileUpload.baseDirectory = null;
         
		// 真实的服务器，用于提供 http 服务
		HttpServer httpServer = HttpServer.build(properties);
		return new ReactiveServer(httpServer, handler);
	}
}