/*
 *  Copyright (c) 2011-2016 The original author or authors
 *  ------------------------------------------------------
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *       The Eclipse Public License is available at
 *       http://www.eclipse.org/legal/epl-v10.html
 *
 *       The Apache License v2.0 is available at
 *       http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package com.swak.config.zookeeper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.zookeeper.CreateMode;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.impl.ContextImpl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.Counter;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.spi.cluster.AsyncMultiMap;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.core.spi.cluster.NodeListener;
import io.vertx.spi.cluster.zookeeper.impl.AsyncMapTTLMonitor;
import io.vertx.spi.cluster.zookeeper.impl.ZKAsyncMap;
import io.vertx.spi.cluster.zookeeper.impl.ZKAsyncMultiMap;
import io.vertx.spi.cluster.zookeeper.impl.ZKSyncMap;

/**
 * A cluster manager that uses Zookeeper
 *
 * @author Stream.Liu
 */
public class ZookeeperClusterManager implements ClusterManager, PathChildrenCacheListener {

	private static final Logger log = LoggerFactory.getLogger(ZookeeperClusterManager.class);
	private Vertx vertx;

	private NodeListener nodeListener;
	private PathChildrenCache clusterNodes;
	private volatile boolean active;

	private String nodeID;
	private CuratorFramework curator;
	private boolean customCuratorCluster;
	private RetryPolicy retryPolicy;
	private Map<String, ZKLock> locks = new ConcurrentHashMap<>();
	private Map<String, AsyncMap<?, ?>> asyncMapCache = new ConcurrentHashMap<>();
	private Map<String, AsyncMultiMap<?, ?>> asyncMultiMapCache = new ConcurrentHashMap<>();

	private static final String ZK_PATH_LOCKS = "/locks/";
	private static final String ZK_PATH_COUNTERS = "/counters/";
	private static final String ZK_PATH_CLUSTER_NODE = "/cluster/nodes/";
	private static final String ZK_PATH_CLUSTER_NODE_WITHOUT_SLASH = "/cluster/nodes";

	// just for unit testing
	public ZookeeperClusterManager(RetryPolicy retryPolicy, CuratorFramework curator) {
		Objects.requireNonNull(retryPolicy, "The retry policy cannot be null.");
		Objects.requireNonNull(curator, "The Curator instance cannot be null.");
		this.retryPolicy = retryPolicy;
		this.curator = curator;
		this.nodeID = UUID.randomUUID().toString();
		this.customCuratorCluster = true;
	}

	public CuratorFramework getCuratorFramework() {
		return this.curator;
	}

	@Override
	public void setVertx(Vertx vertx) {
		this.vertx = vertx;
	}

	/**
	 * Every eventbus handler has an ID. SubsMap (subscriber map) is a MultiMap
	 * which maps handler-IDs with server-IDs and thus allows the eventbus to
	 * determine where to send messages.
	 *
	 * @param name
	 *            A unique name by which the the MultiMap can be identified within
	 *            the cluster. See the cluster config file (e.g.
	 *            io.vertx.spi.cluster.impl.zookeeper.zookeeper.properties in case
	 *            of ZookeeperClusterManager) for additional MultiMap config
	 *            parameters.
	 * @return subscription map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <K, V> void getAsyncMultiMap(String name, Handler<AsyncResult<AsyncMultiMap<K, V>>> handler) {
		vertx.executeBlocking(event -> {
			AsyncMultiMap asyncMultiMap = asyncMultiMapCache.computeIfAbsent(name,
					key -> new ZKAsyncMultiMap<>(vertx, curator, name));
			event.complete(asyncMultiMap);
		}, handler);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <K, V> void getAsyncMap(String name, Handler<AsyncResult<AsyncMap<K, V>>> handler) {
		AsyncMapTTLMonitor<K, V> asyncMapTTLMonitor = AsyncMapTTLMonitor.getInstance(vertx, this);
		vertx.executeBlocking(event -> {
			AsyncMap zkAsyncMap = asyncMapCache.computeIfAbsent(name,
					key -> new ZKAsyncMap<>(vertx, curator, asyncMapTTLMonitor, name));
			event.complete(zkAsyncMap);
		}, handler);
	}

	@Override
	public <K, V> Map<K, V> getSyncMap(String name) {
		return new ZKSyncMap<>(curator, name);
	}

	@Override
	public void getLockWithTimeout(String name, long timeout, Handler<AsyncResult<Lock>> resultHandler) {
		ContextImpl context = (ContextImpl) vertx.getOrCreateContext();
		// Ordered on the internal blocking executor
		context.executeBlocking(() -> {
			ZKLock lock = locks.get(name);
			if (lock == null) {
				InterProcessSemaphoreMutex mutexLock = new InterProcessSemaphoreMutex(curator, ZK_PATH_LOCKS + name);
				lock = new ZKLock(mutexLock);
			}
			try {
				if (lock.getLock().acquire(timeout, TimeUnit.MILLISECONDS)) {
					locks.putIfAbsent(name, lock);
					return lock;
				} else {
					throw new VertxException("Timed out waiting to get lock " + name);
				}
			} catch (Exception e) {
				throw new VertxException("get lock exception", e);
			}
		}, resultHandler);
	}

	@Override
	public void getCounter(String name, Handler<AsyncResult<Counter>> resultHandler) {
		vertx.executeBlocking(future -> {
			try {
				Objects.requireNonNull(name);
				future.complete(new ZKCounter(name, retryPolicy));
			} catch (Exception e) {
				future.fail(new VertxException(e));
			}
		}, resultHandler);
	}

	@Override
	public String getNodeID() {
		return nodeID;
	}

	@Override
	public List<String> getNodes() {
		return clusterNodes.getCurrentData().stream().map(e -> new String(e.getData())).collect(Collectors.toList());
	}

	@Override
	public void nodeListener(NodeListener listener) {
		this.nodeListener = listener;
	}

	private void addLocalNodeID() throws VertxException {
		clusterNodes = new PathChildrenCache(curator, ZK_PATH_CLUSTER_NODE_WITHOUT_SLASH, true);
		clusterNodes.getListenable().addListener(this);
		try {
			clusterNodes.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
			// Join to the cluster
			curator.create().withMode(CreateMode.EPHEMERAL).forPath(ZK_PATH_CLUSTER_NODE + nodeID, nodeID.getBytes());
		} catch (Exception e) {
			throw new VertxException(e);
		}
	}

	@Override
	public synchronized void join(Handler<AsyncResult<Void>> resultHandler) {
		vertx.executeBlocking(future -> {
			if (!active) {
				active = true;

				// The curator instance has been passed using the constructor.
				if (customCuratorCluster) {
					try {
						addLocalNodeID();
						future.complete();
					} catch (VertxException e) {
						future.fail(e);
					}
					return;
				}

				curator.start();
				while (curator.getState() != CuratorFrameworkState.STARTED) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						if (curator.getState() != CuratorFrameworkState.STARTED) {
							future.fail("zookeeper client being interrupted while starting.");
						}
					}
				}
				nodeID = UUID.randomUUID().toString();
				try {
					addLocalNodeID();
					future.complete();
				} catch (Exception e) {
					future.fail(e);
				}
			}
		}, resultHandler);
	}

	@Override
	public void leave(Handler<AsyncResult<Void>> resultHandler) {
		vertx.executeBlocking(future -> {
			synchronized (ZookeeperClusterManager.this) {
				if (active) {
					active = false;
					try {
						curator.delete().deletingChildrenIfNeeded().inBackground((client, event) -> {
							if (event.getType() == CuratorEventType.DELETE) {
								if (customCuratorCluster) {
									future.complete();
								} else {
									if (curator.getState() == CuratorFrameworkState.STARTED) {
										curator.close();
										future.complete();
									}
								}
							}
						}).forPath(ZK_PATH_CLUSTER_NODE + nodeID);
						AsyncMapTTLMonitor.getInstance(vertx, this).stop();
					} catch (Exception e) {
						log.error(e);
						future.fail(e);
					}
				} else
					future.complete();
			}
		}, resultHandler);
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	@SuppressWarnings({"incomplete-switch" })
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
		if (!active)
			return;
		switch (event.getType()) {
		case CHILD_ADDED:
			try {
				if (nodeListener != null) {
					nodeListener.nodeAdded(new String(event.getData().getData()));
				}
			} catch (Throwable t) {
				log.error("Failed to handle memberAdded", t);
			}
			break;
		case CHILD_REMOVED:
			try {
				if (nodeListener != null) {
					nodeListener.nodeLeft(new String(event.getData().getData()));
				}
			} catch (Throwable t) {
				log.error("Failed to handle memberRemoved", t);
			}
			break;
		case CHILD_UPDATED:
			log.warn("Weird event that update cluster node. path:" + event.getData().getPath());
			break;
		case CONNECTION_SUSPENDED:
			// just release locks on this node.
			locks.values().forEach(ZKLock::release);
			break;
		case CONNECTION_LOST:
			// release locks and clean locks
			locks.values().forEach(ZKLock::release);
			locks.clear();
			break;
		}
	}

	/**
	 * Counter implement
	 */
	private class ZKCounter implements Counter {

		private DistributedAtomicLong atomicLong;
		private String counterPath;

		public ZKCounter(String nodeName, RetryPolicy retryPolicy) throws Exception {
			this.counterPath = ZK_PATH_COUNTERS + nodeName;
			this.atomicLong = new DistributedAtomicLong(curator, counterPath, retryPolicy);
		}

		@Override
		public void get(Handler<AsyncResult<Long>> resultHandler) {
			Objects.requireNonNull(resultHandler);
			vertx.executeBlocking(future -> {
				try {
					future.complete(atomicLong.get().preValue());
				} catch (Exception e) {
					future.fail(new VertxException(e));
				}
			}, resultHandler);
		}

		@Override
		public void incrementAndGet(Handler<AsyncResult<Long>> resultHandler) {
			Objects.requireNonNull(resultHandler);
			increment(true, resultHandler);
		}

		@Override
		public void getAndIncrement(Handler<AsyncResult<Long>> resultHandler) {
			increment(false, resultHandler);
		}

		private void increment(boolean post, Handler<AsyncResult<Long>> resultHandler) {
			Objects.requireNonNull(resultHandler);
			vertx.executeBlocking(future -> {
				try {
					long returnValue = 0;
					if (atomicLong.get().succeeded())
						returnValue = atomicLong.get().preValue();
					if (atomicLong.increment().succeeded()) {
						future.complete(post ? atomicLong.get().postValue() : returnValue);
					} else {
						future.fail(new VertxException("increment value failed."));
					}
				} catch (Exception e) {
					future.fail(new VertxException(e));
				}
			}, resultHandler);
		}

		@Override
		public void decrementAndGet(Handler<AsyncResult<Long>> resultHandler) {
			Objects.requireNonNull(resultHandler);
			vertx.executeBlocking(future -> {
				try {
					if (atomicLong.decrement().succeeded()) {
						future.complete(atomicLong.get().postValue());
					} else {
						future.fail(new VertxException("decrement value failed."));
					}
				} catch (Exception e) {
					future.fail(new VertxException(e));
				}
			}, resultHandler);
		}

		@Override
		public void addAndGet(long value, Handler<AsyncResult<Long>> resultHandler) {
			add(value, true, resultHandler);
		}

		@Override
		public void getAndAdd(long value, Handler<AsyncResult<Long>> resultHandler) {
			add(value, false, resultHandler);
		}

		private void add(long value, boolean post, Handler<AsyncResult<Long>> resultHandler) {
			Objects.requireNonNull(resultHandler);
			vertx.executeBlocking(future -> {
				try {
					long returnValue = 0;
					if (atomicLong.get().succeeded())
						returnValue = atomicLong.get().preValue();
					if (atomicLong.add(value).succeeded()) {
						future.complete(post ? atomicLong.get().postValue() : returnValue);
					} else {
						future.fail(new VertxException("add value failed."));
					}
				} catch (Exception e) {
					future.fail(new VertxException(e));
				}
			}, resultHandler);
		}

		@Override
		public void compareAndSet(long expected, long value, Handler<AsyncResult<Boolean>> resultHandler) {
			Objects.requireNonNull(resultHandler);
			vertx.executeBlocking(future -> {
				try {
					if (atomicLong.get().succeeded() && atomicLong.get().preValue() == 0)
						this.atomicLong.initialize(0L);
					future.complete(atomicLong.compareAndSet(expected, value).succeeded());
				} catch (Exception e) {
					future.fail(new VertxException(e));
				}
			}, resultHandler);
		}
	}

	/**
	 * Lock implement
	 */
	private class ZKLock implements Lock {

		private final InterProcessSemaphoreMutex lock;

		private ZKLock(InterProcessSemaphoreMutex lock) {
			this.lock = lock;
		}

		InterProcessSemaphoreMutex getLock() {
			return lock;
		}

		@Override
		public void release() {
			vertx.executeBlocking(future -> {
				try {
					lock.release();
				} catch (Exception e) {
					log.error(e);
				}
				future.complete();
			}, false, null);
		}
	}
}