package com.swak.config.vertx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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
@ConditionalOnClass(ReactiveServer.class)
@ConditionalOnMissingBean(StandardVertx.class)
@EnableConfigurationProperties(VertxProperties.class)
public class StandardVerxAutoConfiguration {

	/**
	 * 构建配置
	 * 
	 * @param properties
	 * @return
	 */
	@Bean
	public VertxOptions vertxOptions(VertxProperties properties) {
		VertxOptions vertxOptions = new VertxOptions();
		if (properties.getMode() == TransportMode.EPOLL) {
			vertxOptions.setPreferNativeTransport(true);
		}
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