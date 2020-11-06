package com.swak.config.vertx;

import static com.swak.Application.APP_LOGGER;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.customizer.VertxOptionsCustomizer;
import com.swak.reactivex.transport.TransportMode;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.transport.VertxProxy;
import com.swak.vertx.transport.codec.MsgCodec;
import com.swak.vertx.transport.server.ReactiveServer;
import com.swak.vertx.transport.vertx.StandardVertx;

import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.file.FileSystemOptions;

/**
 * 配置单机版本的 vertx
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@ConditionalOnMissingBean(StandardVertx.class)
@EnableConfigurationProperties(VertxProperties.class)
public class StandardVerxAutoConfiguration {

	private VertxProperties properties;
	private List<VertxOptionsCustomizer> customizers;

	public StandardVerxAutoConfiguration(VertxProperties properties,
			ObjectProvider<List<VertxOptionsCustomizer>> customizersProvider) {
		APP_LOGGER.debug("Loading Standard Vertx");
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
		this.properties = properties;
		this.customizers = customizersProvider.getIfAvailable();
	}

	/**
	 * 构建配置
	 * 
	 * @param properties
	 * @return
	 */
	@Bean
	public VertxOptions vertxOptions() {
		VertxOptions vertxOptions = new VertxOptions();
		if (properties.getMode() == TransportMode.EPOLL) {
			vertxOptions.setPreferNativeTransport(true);
		}
		// HttpHandlers 根据 HttpServerImpl 的值 DISABLE_WEBSOCKETS 来判断 是否需要禁用 websocket
		// 目前是无法自定义的， 可以在真实启用 Websocket 在开启这个参数
		if (!properties.isEnableWebsocket()) {
			System.setProperty("vertx.disableWebsockets", Boolean.TRUE.toString());
		}
		if (!properties.isEnableHttp2()) {
			System.setProperty("vertx.disableH2c", Boolean.TRUE.toString());
		}
		vertxOptions.setEventLoopPoolSize(properties.getEventLoopPoolSize());
		vertxOptions.setWorkerPoolSize(properties.getWorkerThreads());
		vertxOptions.setInternalBlockingPoolSize(properties.getInternalBlockingThreads());
		vertxOptions.setMaxEventLoopExecuteTime(properties.getMaxEventLoopExecuteTime());
		vertxOptions.setMaxWorkerExecuteTime(properties.getMaxWorkerExecuteTime());
		FileSystemOptions fileSystemOptions = new FileSystemOptions();
		fileSystemOptions.setClassPathResolvingEnabled(properties.isClassPathResolvingEnabled());
		fileSystemOptions.setFileCachingEnabled(properties.isFileCachingEnabled());
		fileSystemOptions.setFileCacheDir(properties.getFileCacheDir());
		vertxOptions.setFileSystemOptions(fileSystemOptions);
		if (this.customizers != null && !this.customizers.isEmpty()) {
			for (VertxOptionsCustomizer customizer : this.customizers) {
				customizer.customize(vertxOptions);
			}
		}
		return vertxOptions;
	}

	/**
	 * 构建配置
	 * 
	 * @param properties
	 * @return
	 */
	@Bean
	public DeliveryOptions deliveryOptions(VertxProperties properties) {
		DeliveryOptions deliveryOptions = new DeliveryOptions();
		deliveryOptions.setSendTimeout(properties.getSendTimeout());
		deliveryOptions.setCodecName(MsgCodec.CODEC_NAME);
		return deliveryOptions;
	}

	/**
	 * 配置标准的 vertx
	 * 
	 * @param properties
	 * @return
	 */
	@Bean
	public VertxProxy standardVertx(VertxOptions vertxOptions, DeliveryOptions deliveryOptions) {
		return new StandardVertx(vertxOptions, deliveryOptions);
	}
}