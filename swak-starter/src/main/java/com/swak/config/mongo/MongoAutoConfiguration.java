package com.swak.config.mongo;

import static com.swak.Application.APP_LOGGER;

import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.bson.codecs.BooleanCodec;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DoubleCodec;
import org.bson.codecs.IntegerCodec;
import org.bson.codecs.LongCodec;
import org.bson.codecs.StringCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.async.client.MongoClient;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import com.swak.mongo.MongoClients;
import com.swak.mongo.MongoClients.MongoHolder;
import com.swak.mongo.codec.DocumentCodec;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.transport.TransportMode;
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

	public MongoAutoConfiguration() {
		APP_LOGGER.debug("Loading Mongo");
	}

	/**
	 * 配置项 mongo 只能使用 NIO, 不支持EPOLL <br>
	 * 
	 * @return
	 */
	@Bean
	public MongoClientSettings settings() {
		CodecRegistry commonCodecRegistry = CodecRegistries.fromCodecs(new StringCodec(), new IntegerCodec(),
				new BooleanCodec(), new DoubleCodec(), new LongCodec(), new BsonDocumentCodec());
		LoopResources loopResources = Contexts.createEventLoopResources(TransportMode.NIO, 1, -1, "Mongodb.", true, 2,
				TimeUnit.SECONDS);
		EventLoopGroup eventLoopGroup = loopResources.onClient();
		return MongoClientSettings.builder()
				.streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build())
				.codecRegistry(CodecRegistries.fromRegistries(commonCodecRegistry,
						CodecRegistries.fromCodecs(new DocumentCodec(false))))
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
		MongoHolder holder = new MongoHolder(this.mongo, this.mongo.getDatabase(properties.getDatabase()));
		MongoClients.setMongoDB(holder);
		return this.mongo;
	}

	/**
	 * 停止释放资源
	 */
	@PreDestroy
	public void close() {
		if (this.mongo != null) {
			this.mongo.close();
		}
	}
}