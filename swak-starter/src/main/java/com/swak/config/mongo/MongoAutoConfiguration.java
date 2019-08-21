package com.swak.config.mongo;

import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.async.client.MongoClient;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.transport.resources.LoopResources;

import io.netty.channel.EventLoopGroup;

/**
 * 异步的客户端
 * 
 * @author lifeng
 */
@SuppressWarnings("deprecation")
@Configuration
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
public class MongoAutoConfiguration {

	private MongoClient mongo;

	@Autowired
	private MongoProperties properties;

	@PreDestroy
	public void close() {
		if (this.mongo != null) {
			this.mongo.close();
		}
	}

	/**
	 * 配置项
	 * 
	 * @return
	 */
	@Bean
	public MongoClientSettings settings() {
		LoopResources loopResources = Contexts.createEventLoopResources(properties.getMode(), 1, -1, "Mongodb.", true,
				2, TimeUnit.SECONDS);
		EventLoopGroup eventLoopGroup = loopResources.onClient();
		return MongoClientSettings.builder()
				.streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build())
				.build();
	}

	/**
	 * 客户端
	 * 
	 * @param settings
	 * @return
	 */
	@Bean
	public MongoClient mongoClient(MongoClientSettings settings) {
		MongoClientFactory factory = new MongoClientFactory(properties);
		this.mongo = factory.createMongoClient(settings);
		return this.mongo;
	}
}