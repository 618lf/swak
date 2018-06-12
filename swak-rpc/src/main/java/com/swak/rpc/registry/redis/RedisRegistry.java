package com.swak.rpc.registry.redis;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.factory.RedisClientDecorator;
import com.swak.cache.redis.factory.RedisConnectionFactory;
import com.swak.cache.redis.factory.RedisConnectionPoolFactory;
import com.swak.rpc.api.Constants;
import com.swak.rpc.api.URL;
import com.swak.rpc.registry.FailbackRegistry;
import com.swak.rpc.registry.NotifyListener;
import com.swak.utils.Sets;

import io.lettuce.core.RedisClient;

/**
 * 将redis 实现为订阅管理服务器
 * @author lifeng
 */
public class RedisRegistry extends FailbackRegistry {

	private AsyncOperations asyncOperations;
	private final int expirePeriod;
	private NotifySub notifySub; // 通知管理
	
	public RedisRegistry(RedisClient redisClient) {
		RedisConnectionFactory<byte[], byte[]> poolFactory = new RedisConnectionPoolFactory(new RedisClientDecorator(redisClient));
		this.asyncOperations = new AsyncOperations(poolFactory);
		this.expirePeriod = Constants.DEFAULT_SESSION_TIMEOUT;
	}

	/**
	 * 执行注册
	 */
	@Override
	protected CompletionStage<Boolean> doRegister(URL url) {
		String serviceKey = url.getSequence();
		String serviceUrl = url.toString();
		String expire = String.valueOf(System.currentTimeMillis() + expirePeriod);
		return asyncOperations.hSet(serviceKey, serviceUrl, SafeEncoder.encode(expire))
		.thenCompose(s -> asyncOperations.publish(serviceKey, Constants.REGISTER))
		.thenApply(s -> s > 0);
	}
	
	/**
	 * 取消注册
	 */
	@Override
    public CompletionStage<Boolean> doUnregister(URL url) {
		String serviceKey = url.getSequence();
		String serviceUrl = url.toString();
		return asyncOperations.hDel(serviceKey, serviceUrl)
				.thenCompose(s -> asyncOperations.publish(serviceKey, Constants.UNREGISTER))
				.thenApply(s -> s > 0);
	}
	
	/**
	 * 订阅服务，注册监听事件
	 */
	@Override
    public CompletionStage<Void> doSubscribe(String serviceKey, NotifyListener listener) {
		initNotify();
		return this.doNotify(serviceKey, Sets.newHashSet(listener));
	}
	
	/**
	 * 取消订阅，
	 */
	@Override
    public CompletionStage<Void> doUnsubscribe(String serviceKey, NotifyListener listener) {
		return null;
	}
	
	// 初始化订阅管理
	private void initNotify() {
		if (notifySub == null) {
			notifySub = new NotifySub(Constants.REDIS_SUBSCRIBE_TOPIC);
			notifySub.subscribe();
		}
	}
	
    private CompletionStage<Void> doNotify(String key) {
		return this.doNotify(key, this.getSubscribed().get(key));
	}
	
	private CompletionStage<Void> doNotify(String key, Set<NotifyListener> listeners) {
		return asyncOperations.hGetAll(key).thenApply(map ->{
			return map.keySet().stream()
					.map(bs -> SafeEncoder.encode(bs))
					.map(k -> URL.valueOf(k))
					.collect(Collectors.toList());
		}).thenAccept(urls ->{
			listeners.stream().forEach(lis -> this.notify(key, lis, urls));
		});
	}
	
	public class NotifySub extends PubSubListenerAdapter<NotifyEvent> {
		
		private String channel;
		public NotifySub(String channel) {
			this.channel = channel;
		}
		
		@Override
		public void subscribe() {
			asyncOperations.listener(this);
			asyncOperations.subscribe(channel);
		}

		/**
		 * 监听到变化之后，作出响应
		 */
		@Override
		public void onMessage(String channel, NotifyEvent message) {
			doNotify(message.getKey());
		}
	}
	
	/**
	 * 订阅事件
	 * @author lifeng
	 */
	public class NotifyEvent implements Serializable {
		private static final long serialVersionUID = 1L;
		private String key;
		private String type;
		public NotifyEvent(String key, String type) {
			this.key = key;
			this.type = type;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
	}
}