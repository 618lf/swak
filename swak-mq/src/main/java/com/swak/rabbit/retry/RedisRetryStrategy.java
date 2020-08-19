package com.swak.rabbit.retry;

import com.swak.SafeEncoder;
import com.swak.rabbit.message.PendingConfirm;
import com.swak.redis.RedisCommands;
import com.swak.redis.RedisService;
import com.swak.serializer.SerializationUtils;

/**
 * 将消息存储在 Redis中，保证重启动消息不丢失 需要保证Redis 的高可用 可以使用全局的任务来重试消息
 * 
 * @author lifeng
 */
public class RedisRetryStrategy extends AbstractRetryStrategy {

	private String confirms = "Retry.Ids";
	private String _confirms = "Retry.Confirms";
	private RedisCommands<byte[], byte[]> redisCommands;

	public RedisRetryStrategy(RedisService redisService) {
		redisCommands = redisService.sync();
	}

	@Override
	public void add(PendingConfirm pendingConfirm) {
		if (redisCommands.sAdd(confirms, SafeEncoder.encode(pendingConfirm.getId()))) {
			redisCommands.lPush(_confirms, SerializationUtils.serialize(pendingConfirm));
		}
	}

	@Override
	public void del(String id) {
		redisCommands.sRem(confirms, SafeEncoder.encode(id));
	}

	@Override
	protected PendingConfirm headGet() {
		byte[] value = redisCommands.rGet(confirms);
		return value != null ? (PendingConfirm) SerializationUtils.deserialize(value) : null;
	}

	@Override
	protected boolean hasAcked(PendingConfirm pendingConfirm) {
		return redisCommands.sExists(confirms, SafeEncoder.encode(pendingConfirm.getId()));
	}

	@Override
	protected void headRemove() {
		byte[] value = redisCommands.rPop(confirms);
		if (value != null) {
			PendingConfirm pendingConfirm = (PendingConfirm) SerializationUtils.deserialize(value);
			this.del(pendingConfirm.getId());
		}
	}
}