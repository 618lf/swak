package com.swak.rabbit.retry;

import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.operations.SyncOperations;
import com.swak.rabbit.RabbitMQTemplate;
import com.swak.rabbit.message.PendingConfirm;
import com.swak.serializer.SerializationUtils;

/**
 * 将消息存储在 Redis中，保证重启动消息不丢失 需要保证Redis 的高可用 可以使用全局的任务来重试消息
 * 
 * @author lifeng
 */
public class RedisRetryStrategy extends AbstractRetryStrategy {

	private final RabbitMQTemplate template;
	private String confirms = "Retry.Ids";
	private String _confirms = "Retry.Confirms";

	public RedisRetryStrategy(RabbitMQTemplate template) {
		this.template = template;
	}

	@Override
	public RabbitMQTemplate getSender() {
		return template;
	}

	@Override
	public void add(PendingConfirm pendingConfirm) {
		if (SyncOperations.sAdd(confirms, SafeEncoder.encode(pendingConfirm.getId()))) {
			SyncOperations.lPush(_confirms, SerializationUtils.serialize(pendingConfirm));
		}
	}

	@Override
	public void del(String id) {
		SyncOperations.sRem(confirms, SafeEncoder.encode(id));
	}

	@Override
	protected PendingConfirm headGet() {
		byte[] value = SyncOperations.rGet(confirms);
		return value != null ? (PendingConfirm) SerializationUtils.deserialize(value) : null;
	}

	@Override
	protected boolean hasAcked(PendingConfirm pendingConfirm) {
		return SyncOperations.sExists(confirms, SafeEncoder.encode(pendingConfirm.getId()));
	}

	@Override
	protected void headRemove() {
		byte[] value = SyncOperations.rPop(confirms);
		if (value != null) {
			PendingConfirm pendingConfirm = (PendingConfirm) SerializationUtils.deserialize(value);
			this.del(pendingConfirm.getId());
		}
	}
}