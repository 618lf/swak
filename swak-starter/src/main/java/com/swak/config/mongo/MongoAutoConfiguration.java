package com.swak.config.mongo;

import static com.swak.Application.APP_LOGGER;
import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.bson.BsonType;
import org.bson.codecs.BsonCodecProvider;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.IterableCodecProvider;
import org.bson.codecs.MapCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.jsr310.Jsr310CodecProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.DocumentToDBRefTransformer;
import com.mongodb.MongoClientSettings;
import com.mongodb.async.client.MongoClient;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import com.swak.mongo.MongoClients;
import com.swak.mongo.MongoClients.MongoHolder;
import com.swak.mongo.codec.DocumentCodecxProvider;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.resources.LoopResources;
import com.swak.utils.Maps;

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

	public MongoAutoConfiguration() {
		APP_LOGGER.debug("Loading Mongodb");
	}

	/**
	 * 对应的转换器
	 * 
	 * @return
	 */
	@Bean
	public BsonTypeClassMap bsonTypeClassMap() {
		Map<BsonType, Class<?>> replacementsForDefaults = Maps.newHashMap();
		replacementsForDefaults.put(BsonType.DATE_TIME, LocalDateTime.class);
		return new BsonTypeClassMap(replacementsForDefaults);
	}

	/**
	 * 配置项 mongo 只能使用 NIO, 不支持EPOLL <br>
	 * 
	 * @return
	 */
	@Bean
	public MongoClientSettings settings(BsonTypeClassMap bsonTypeClassMap) {
		LoopResources loopResources = Contexts.createEventLoopResources(TransportMode.NIO, 1, -1, "Mongodb.", true, 2,
				TimeUnit.SECONDS);
		EventLoopGroup eventLoopGroup = loopResources.onClient();
		return MongoClientSettings.builder()
				.streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build())
				.codecRegistry(fromProviders(asList(new ValueCodecProvider(), new BsonValueCodecProvider(),
						new Jsr310CodecProvider(), new DocumentCodecxProvider(bsonTypeClassMap),
						new IterableCodecProvider(new DocumentToDBRefTransformer()),
						new MapCodecProvider(new DocumentToDBRefTransformer()), new BsonCodecProvider())))
				.build();
	}

	/**
	 * 客户端
	 * 
	 * @param settings
	 * @return
	 */
	@Bean
	public MongoClient mongoClient(MongoClientSettings settings, MongoProperties properties) {
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