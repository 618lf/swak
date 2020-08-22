package com.swak.redis;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.SafeEncoder;
import com.swak.exception.TimeOutException;
import com.swak.lock.AsyncLock;
import com.swak.reactivex.threads.TimerContext.ScheduledTimerTask;

/**
 * 异步锁
 * 
 * @author lifeng
 * @date 2020年8月21日 下午11:58:10
 */
public class AsyncRedisLock implements AsyncLock, MessageListener {

	private static final ConcurrentMap<String, ExpirationEntry> EXPIRATION_RENEWAL_MAP = new ConcurrentHashMap<>();
	private static Logger log = LoggerFactory.getLogger(AsyncRedisLock.class);

	private LinkedTransferQueue<AsyncRedisLockTask> queue;
	private RedisAsyncCommands<byte[], byte[]> redisAsync;
	private RedisAsyncPubSubCommands<byte[], byte[]> redisAsyncPubSub;
	final String name;
	final long expirationTimeInMilliseconds;
	final String id;
	private final Semaphore semaphore;
	private RedisService redisService;

	AsyncRedisLock(RedisService redisService, String name) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.redisAsync = redisService.async();
		this.redisAsyncPubSub = redisService.event();
		this.expirationTimeInMilliseconds = 5 * 1000L; // 5秒
		this.semaphore = new Semaphore(1);
		this.redisService = redisService;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public CompletableFuture<Void> lock(Long lockId) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		this.tryAcquireAsync(lockId).whenComplete((ttl, e) -> {
			if (ttl == null) {
				future.complete(null);
				return;
			}
			if (e != null) {
				future.completeExceptionally(e);
				return;
			}
			AsyncRedisLockTask task = new AsyncRedisLockTask(lockId, future);
			this.queue.add(task);
			this.tryLock();
		});
		return future;
	}

	/**
	 * 开启任务尝试占用锁
	 */
	private void tryLock() {

		// 控制并发度
		if (!this.semaphore.tryAcquire()) {
			return;
		}

		// 获取一个任务
		AsyncRedisLockTask task = this.queue.poll();

		// 如果没有任务则不需要处理
		if (task == null) {
			this.semaphore.release();
			return;
		}

		// 开启订阅
		this.subscribe().whenComplete((r, e) -> {

			// 尝试占用
			this.tryAcquireAsync(task.lockId).whenComplete((ttl, e1) -> {

				// 占用成功
				if (ttl == null) {
					task.future.complete(null);
					// 释放
					this.semaphore.release();
					return;
				}

				// 重新放回队列
				this.queue.add(task);

				// 释放
				this.semaphore.release();
			});
		});
	}

	/**
	 * 消息通知有人释放锁
	 */
	@Override
	public void onMessage(String channel, byte[] message) {
		if (!this.getLockTopic().equals(channel)) {
			return;
		}
		this.tryLock();
	}

	@Override
	public CompletableFuture<Void> unlock(Long lockId) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		this.unlockAsync(lockId).whenComplete((t, e) -> {
			future.complete(null);
			this.tryLock();
		});
		return future;
	}

	/**
	 * 订阅
	 * 
	 * @return
	 */
	private CompletionStage<Void> subscribe() {
		CompletionStage<Void> future = redisAsyncPubSub.subscribe(this, SafeEncoder.encode(this.getLockTopic()));
		this.redisService.newTimeoutTask(() -> {
			CompletableFuture<Void> completableFuture = future.toCompletableFuture();
			if (!completableFuture.isDone() && completableFuture.isCancelled()) {
				completableFuture.completeExceptionally(new TimeOutException("等待消息超时！"));
			}
		}, expirationTimeInMilliseconds, TimeUnit.MILLISECONDS);
		return future;
	}

	/**
	 * 尝试释放锁
	 * 
	 * @param threadId
	 * @return
	 */
	private CompletionStage<Void> unlockAsync(long threadId) {
		CompletableFuture<Void> future = new CompletableFuture<>();

		this.unlockInnerAsync(threadId).whenComplete((opStatus, e) -> {

			// 取消本地刷新
			this.cancelExpirationRenewal(threadId);

			if (e != null) {
				future.completeExceptionally(e);
				return;
			}

			if (opStatus == null) {
				IllegalMonitorStateException cause = new IllegalMonitorStateException(
						"attempt to unlock lock, not locked by current thread by node id: " + id + " thread-id: "
								+ threadId);
				future.completeExceptionally(cause);
				return;
			}
			future.complete(null);
		});

		return future;
	}

	/**
	 * 如果获得锁则加入定时任务刷新时间
	 * 
	 * @return
	 */
	private CompletionStage<Long> tryAcquireAsync(long threadId) {
		String threadName = this.getThreadId(threadId);
		CompletionStage<Long> fuCompletionStage = this.tryLockAsync(name, expirationTimeInMilliseconds, threadName);
		return fuCompletionStage.whenComplete((ttl, e) -> {
			if (ttl == null) {
				scheduleExpirationRenewal(threadId);
			}
		});
	}

	/**
	 * 定时刷新
	 * 
	 * @param threadId
	 */
	private void scheduleExpirationRenewal(long threadId) {
		ExpirationEntry entry = new ExpirationEntry();
		ExpirationEntry oldEntry = EXPIRATION_RENEWAL_MAP.putIfAbsent(this.id, entry);
		if (oldEntry != null) {
			oldEntry.addThreadId(threadId);
		} else {
			entry.addThreadId(threadId);
			renewExpiration();
		}
	}

	private void renewExpiration() {
		ExpirationEntry ee = EXPIRATION_RENEWAL_MAP.get(this.id);
		if (ee == null) {
			return;
		}
		ScheduledTimerTask task = redisService.newTimeoutTask(() -> {
			ExpirationEntry ent = EXPIRATION_RENEWAL_MAP.get(this.id);
			if (ent == null) {
				return;
			}

			Long threadId = ent.getFirstThreadId();
			if (threadId == null) {
				return;
			}

			// 异步刷新过期时间, 刷新成功持续刷新
			this.renewExpirationAsync(threadId).whenComplete((ttl, e) -> {
				if (e != null) {
					log.error("Can't update lock " + this.name() + " expiration", e);
					return;
				}
				if (ttl) {
					renewExpiration();
				}
			});
		}, this.expirationTimeInMilliseconds / 3, TimeUnit.MILLISECONDS);

		ee.setTimeout(task);
	}

	private void cancelExpirationRenewal(Long threadId) {
		ExpirationEntry task = EXPIRATION_RENEWAL_MAP.get(this.id);
		if (task == null) {
			return;
		}

		if (threadId != null) {
			task.removeThreadId(threadId);
		}

		if (threadId == null || task.hasNoThreads()) {
			ScheduledTimerTask timeout = task.getTimeout();
			if (timeout != null) {
				timeout.cancel();
			}
			EXPIRATION_RENEWAL_MAP.remove(this.id);
		}
	}

	/**
	 * 线程的唯一ID
	 * 
	 * @return
	 */
	private String getThreadId(long threadId) {
		return this.id + ":" + threadId;
	}

	/**
	 * 锁通道
	 * 
	 * @return
	 */
	private String getLockTopic() {
		return this.name + ".topic";
	}

	/**
	 * 尝试释放锁
	 * 
	 * @param threadId
	 * @return
	 */
	private CompletionStage<Long> unlockInnerAsync(long threadId) {
		return this.tryUnLockAsync(name, this.getLockTopic(), expirationTimeInMilliseconds, this.getThreadId(threadId));
	}

	/**
	 * 尝试获取锁：<br/>
	 * 1. 判断 key 是否存在，如果不存在，则将第指定的key +1，表示重入次数 ， 并设置过期时间。<br/>
	 * 2. 判断 key 中的 当前线程 是否存在，如果已存在，添加重入次数，并设置过期时间。<br/>
	 * 3. 返回 key 的 ttl 时间。<br/>
	 */
	public CompletionStage<Long> tryLockAsync(String key, long expirationTimeInMilliseconds, String threadId) {
		byte[][] keys = new byte[][] { SafeEncoder.encode(key) };
		byte[][] argvs = new byte[][] { SafeEncoder.encode(String.valueOf(expirationTimeInMilliseconds)),
				SafeEncoder.encode(threadId) };
		return redisAsync.runScript(
				"if (redis.call('exists', KEYS[1]) == 0) then " + "redis.call('hincrby', KEYS[1], ARGV[2], 1); "
						+ "redis.call('pexpire', KEYS[1], ARGV[1]); " + "return nil; " + "end; "
						+ "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then "
						+ "redis.call('hincrby', KEYS[1], ARGV[2], 1); " + "redis.call('pexpire', KEYS[1], ARGV[1]); "
						+ "return nil; " + "end; " + "return redis.call('pttl', KEYS[1]);",
				ReturnType.INTEGER, keys, argvs);

	}

	/**
	 * 尝试释放锁：<br/>
	 * 1. 如果当前线程锁不存在，则直接返回<br/>
	 * 2. 重入锁减少， 如果重入锁依旧大于0，表示任然占用锁，设置过期时间<br/>
	 * 3. 否则删除锁，且发布通知表示锁释放<br/>
	 * 
	 * @param key
	 */
	public CompletionStage<Long> tryUnLockAsync(String key, String topic, long expirationTimeInMilliseconds,
			String threadId) {
		byte[][] keys = new byte[][] { SafeEncoder.encode(key) };
		byte[][] argvs = new byte[][] { SafeEncoder.encode(topic),
				SafeEncoder.encode(String.valueOf(expirationTimeInMilliseconds)), SafeEncoder.encode(threadId) };
		return redisAsync.runScript("if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then " + "return nil;" + "end; "
				+ "local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); " + "if (counter > 0) then "
				+ "redis.call('pexpire', KEYS[1], ARGV[2]); " + "return 0; " + "else " + "redis.call('del', KEYS[1]); "
				+ "redis.call('publish', KEYS[2], ARGV[1]); " + "return 1; " + "end; " + "return nil;",
				ReturnType.INTEGER, keys, argvs);

	}

	/**
	 * 刷新过期时间
	 * 
	 * @param threadId
	 * @return
	 */
	public CompletionStage<Boolean> renewExpirationAsync(long threadId) {
		return this.renewExpirationAsync(name, expirationTimeInMilliseconds, this.getThreadId(threadId));
	}

	/**
	 * 刷新过期时间：<br/>
	 * 1. 如果当前线程锁持有锁则刷新时间<br/>
	 * 
	 * @param key
	 */
	public CompletionStage<Boolean> renewExpirationAsync(String key, long expirationTimeInMilliseconds,
			String threadId) {
		byte[][] keys = new byte[][] { SafeEncoder.encode(key) };
		byte[][] argvs = new byte[][] { SafeEncoder.encode(String.valueOf(expirationTimeInMilliseconds)),
				SafeEncoder.encode(threadId) };
		return redisAsync
				.runScript(
						"if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then "
								+ "redis.call('pexpire', KEYS[1], ARGV[1]); " + "return 1; " + "end; " + "return 0;",
						ReturnType.BOOLEAN, keys, argvs);

	}

	/**
	 * 过期实体
	 * 
	 * @author lifeng
	 * @date 2020年8月20日 上午11:58:05
	 */
	public static class ExpirationEntry {
		private volatile ScheduledTimerTask task;
		private final Map<Long, Integer> threadIds = new LinkedHashMap<>();

		public ExpirationEntry() {
			super();
		}

		public synchronized void addThreadId(long threadId) {
			Integer counter = threadIds.get(threadId);
			if (counter == null) {
				counter = 1;
			} else {
				counter++;
			}
			threadIds.put(threadId, counter);
		}

		public synchronized boolean hasNoThreads() {
			return threadIds.isEmpty();
		}

		public synchronized Long getFirstThreadId() {
			if (threadIds.isEmpty()) {
				return null;
			}
			return threadIds.keySet().iterator().next();
		}

		public synchronized void removeThreadId(long threadId) {
			Integer counter = threadIds.get(threadId);
			if (counter == null) {
				return;
			}
			counter--;
			if (counter == 0) {
				threadIds.remove(threadId);
			} else {
				threadIds.put(threadId, counter);
			}
		}

		public void setTimeout(ScheduledTimerTask task) {
			this.task = task;
		}

		public ScheduledTimerTask getTimeout() {
			return task;
		}
	}

	/**
	 * 锁任务
	 * 
	 * @author lifeng
	 * @date 2020年8月22日 上午11:41:16
	 */
	public static class AsyncRedisLockTask {
		Long lockId;
		CompletableFuture<Void> future;

		public AsyncRedisLockTask(Long lockId, CompletableFuture<Void> future) {
			this.lockId = lockId;
			this.future = future;
		}
	}
}
