package com.swak.config.flux;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.flux.config.AnnotationBean;
import com.swak.flux.handler.HttpHandler;
import com.swak.flux.transport.server.HttpServer;
import com.swak.flux.transport.server.HttpServerProperties;
import com.swak.flux.transport.server.ReactiveServer;
import com.swak.flux.verticle.Flux;
import com.swak.flux.verticle.FluxImpl;

import io.netty.handler.codec.http.multipart.DiskFileUpload;

/**
 * 服务器配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@EnableConfigurationProperties(HttpServerProperties.class)
@AutoConfigureAfter({ RouterAutoConfiguration.class })
public class FluxAutoConfiguration {

	private HttpServerProperties properties;

	public FluxAutoConfiguration(HttpServerProperties properties) {
		this.properties = properties;
		APP_LOGGER.debug("Loading Flux");
	}

	/**
	 * webFlux 配置
	 * 
	 * @return
	 */
	@Bean
	public Flux flux() {
		return new FluxImpl(properties);
	}

	/**
	 * 服务加载器
	 * 
	 * @return
	 */
	@Bean
	public AnnotationBean annotationBean(Flux flux) {
		return new AnnotationBean(flux);
	}

	/**
	 * -Dio.netty.leakDetectionLevel=PARANOID 构建 Reactive Server ，需要提供 HttpHandler
	 * 来处理 http 请求
	 * 
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