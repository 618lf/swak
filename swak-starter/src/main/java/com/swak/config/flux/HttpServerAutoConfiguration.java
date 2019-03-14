package com.swak.config.flux;

import static com.swak.Application.APP_LOGGER;

import java.net.Socket;

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

	/**
	 * 端口占用检测
	 * 
	 * @param properties
	 */
	public HttpServerAutoConfiguration(HttpServerProperties properties) {
		this.properties = properties;
		this.adaptablePort();
		APP_LOGGER.debug("Loading Http Server");
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

	/**
	 * 适配端口
	 */
	private void adaptablePort() {
		int port = properties.getPort();
		if (port == -1) {
			int startPort = 80;
			while (!useable(startPort)) {
				startPort++;
			}
			properties.setPort(startPort);
		} else if (!useable(port)) {
			throw new RuntimeException("端口占用");
		}
	}

	// 校验是否可以用
	private boolean useable(int port) {
		try {
			Socket socket = new Socket("127.0.0.1", port);
			socket.close();
			return false;
		} catch (Exception e) {
			return true;
		}
	}
}