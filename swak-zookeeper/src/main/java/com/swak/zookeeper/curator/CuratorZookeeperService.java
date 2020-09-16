package com.swak.zookeeper.curator;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.x.async.AsyncCuratorFramework;
import org.apache.curator.x.async.api.CreateOption;
import org.apache.curator.x.async.api.DeleteOption;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.utils.Sets;
import com.swak.zookeeper.ChildListener;
import com.swak.zookeeper.DataListener;
import com.swak.zookeeper.EventType;
import com.swak.zookeeper.StateListener;
import com.swak.zookeeper.ZookeeperService;

/**
 * Curator ZookeeperService
 * 
 * @author lifeng
 * @date 2020年9月15日 下午8:50:40
 */
@SuppressWarnings("deprecation")
public class CuratorZookeeperService implements ZookeeperService, ConnectionStateListener, Closeable {

	protected static final Logger logger = LoggerFactory.getLogger(ZookeeperService.class);
	protected static final Charset CHARSET = Charset.forName("UTF-8");

	private final Set<StateListener> stateListeners = new CopyOnWriteArraySet<StateListener>();
	private final ConcurrentMap<String, ConcurrentMap<ChildListener, CuratorWatcherImpl>> childListeners = new ConcurrentHashMap<String, ConcurrentMap<ChildListener, CuratorWatcherImpl>>();
	private final ConcurrentMap<String, ConcurrentMap<DataListener, CuratorWatcherImpl>> listeners = new ConcurrentHashMap<String, ConcurrentMap<DataListener, CuratorWatcherImpl>>();
	private Map<String, TreeCache> treeCacheMap = new ConcurrentHashMap<>();

	private final long UNKNOWN_SESSION_ID = -1L;

	private long lastSessionId;
	private CuratorFramework client;
	private AsyncCuratorFramework asyncClient;

	public CuratorZookeeperService(CuratorFramework client) {
		this.client = client;
		this.asyncClient = AsyncCuratorFramework.wrap(client);
		this.client.getConnectionStateListenable().addListener(this);
		this.client.start();
	}

	@Override
	public void addStateListener(StateListener listener) {
		stateListeners.add(listener);
	}

	@Override
	public void removeStateListener(StateListener listener) {
		stateListeners.remove(listener);
	}

	public Set<StateListener> getSessionListeners() {
		return stateListeners;
	}

	protected void triggerStateChanged(int state) {
		for (StateListener sessionListener : getSessionListeners()) {
			sessionListener.stateChanged(state);
		}
	}

	@Override
	public List<String> addChildListener(String path, final ChildListener listener) {
		ConcurrentMap<ChildListener, CuratorWatcherImpl> listeners = childListeners.computeIfAbsent(path,
				k -> new ConcurrentHashMap<>());
		CuratorWatcherImpl targetListener = listeners.computeIfAbsent(listener,
				k -> createTargetChildListener(path, k));
		return addTargetChildListener(path, targetListener);
	}

	@Override
	public void addDataListener(String path, DataListener listener) {
		this.addDataListener(path, listener, null);
	}

	@Override
	public void addDataListener(String path, DataListener listener, Executor executor) {
		ConcurrentMap<DataListener, CuratorWatcherImpl> dataListenerMap = listeners.computeIfAbsent(path,
				k -> new ConcurrentHashMap<>());
		CuratorWatcherImpl targetListener = dataListenerMap.computeIfAbsent(listener,
				k -> createTargetDataListener(path, k));
		addTargetDataListener(path, targetListener, executor);
	}

	@Override
	public void removeDataListener(String path, DataListener listener) {
		ConcurrentMap<DataListener, CuratorWatcherImpl> dataListenerMap = listeners.get(path);
		if (dataListenerMap != null) {
			CuratorWatcherImpl targetListener = dataListenerMap.remove(listener);
			if (targetListener != null) {
				removeTargetDataListener(path, targetListener);
			}
		}
	}

	@Override
	public void removeChildListener(String path, ChildListener listener) {
		ConcurrentMap<ChildListener, CuratorWatcherImpl> listeners = childListeners.get(path);
		if (listeners != null) {
			CuratorWatcherImpl targetListener = listeners.remove(listener);
			if (targetListener != null) {
				removeTargetChildListener(path, targetListener);
			}
		}
	}

	protected CuratorWatcherImpl createTargetChildListener(String path, ChildListener listener) {
		return new CuratorWatcherImpl(client, listener, path);
	}

	protected List<String> addTargetChildListener(String path, CuratorWatcherImpl listener) {
		try {
			return client.getChildren().usingWatcher(listener).forPath(path);
		} catch (NoNodeException e) {
			return null;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	protected CuratorWatcherImpl createTargetDataListener(String path, DataListener listener) {
		return new CuratorWatcherImpl(client, listener);
	}

	protected void addTargetDataListener(String path, CuratorWatcherImpl treeCacheListener) {
		this.addTargetDataListener(path, treeCacheListener, null);
	}

	protected void addTargetDataListener(String path, CuratorWatcherImpl treeCacheListener, Executor executor) {
		try {
			TreeCache treeCache = TreeCache.newBuilder(client, path).setCacheData(false).build();
			treeCacheMap.putIfAbsent(path, treeCache);

			if (executor == null) {
				treeCache.getListenable().addListener(treeCacheListener);
			} else {
				treeCache.getListenable().addListener(treeCacheListener, executor);
			}

			treeCache.start();
		} catch (Exception e) {
			throw new IllegalStateException("Add treeCache listener for path:" + path, e);
		}
	}

	protected void removeTargetDataListener(String path, CuratorWatcherImpl treeCacheListener) {
		TreeCache treeCache = treeCacheMap.get(path);
		if (treeCache != null) {
			treeCache.getListenable().removeListener(treeCacheListener);
		}
		treeCacheListener.dataListener = null;
	}

	public void removeTargetChildListener(String path, CuratorWatcherImpl listener) {
		listener.unwatch();
	}

	@Override
	public void close() throws IOException {
		this.client.close();
	}

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState state) {

		long sessionId = UNKNOWN_SESSION_ID;
		try {
			sessionId = client.getZookeeperClient().getZooKeeper().getSessionId();
		} catch (Exception e) {
			logger.warn("Curator client state changed, but failed to get the related zk session instance.");
		}

		if (state == ConnectionState.LOST) {
			logger.warn("Curator zookeeper session " + Long.toHexString(lastSessionId) + " expired.");
			this.triggerStateChanged(StateListener.SESSION_LOST);
		} else if (state == ConnectionState.SUSPENDED) {
			logger.warn("Curator zookeeper connection of session " + Long.toHexString(sessionId));
			this.triggerStateChanged(StateListener.SUSPENDED);
		} else if (state == ConnectionState.CONNECTED) {
			lastSessionId = sessionId;
			logger.info("Curator zookeeper client instance initiated successfully, session id is "
					+ Long.toHexString(sessionId));
			this.triggerStateChanged(StateListener.CONNECTED);
		} else if (state == ConnectionState.RECONNECTED) {
			if (lastSessionId == sessionId && sessionId != UNKNOWN_SESSION_ID) {
				logger.warn("Curator zookeeper connection recovered from connection lose, " + "reuse the old session "
						+ Long.toHexString(sessionId));
				this.triggerStateChanged(StateListener.RECONNECTED);
			} else {
				logger.warn("New session created after old session lost, " + "old session "
						+ Long.toHexString(lastSessionId) + ", new session " + Long.toHexString(sessionId));
				lastSessionId = sessionId;
				this.triggerStateChanged(StateListener.NEW_SESSION_CREATED);
			}
		}
	}

	static class CuratorWatcherImpl implements CuratorWatcher, TreeCacheListener {

		private CuratorFramework client;
		private volatile ChildListener childListener;
		private volatile DataListener dataListener;
		private String path;

		public CuratorWatcherImpl(CuratorFramework client, ChildListener listener, String path) {
			this.client = client;
			this.childListener = listener;
			this.path = path;
		}

		public CuratorWatcherImpl(CuratorFramework client, DataListener dataListener) {
			this.dataListener = dataListener;
		}

		protected CuratorWatcherImpl() {
		}

		public void unwatch() {
			this.childListener = null;
		}

		@Override
		public void process(WatchedEvent event) throws Exception {
			// if client connect or disconnect to server, zookeeper will queue
			// watched event(Watcher.Event.EventType.None, .., path = null).
			if (event.getType() == Watcher.Event.EventType.None) {
				return;
			}

			if (childListener != null) {
				childListener.childChanged(path, client.getChildren().usingWatcher(this).forPath(path));
			}
		}

		@Override
		public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
			if (dataListener != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("listen the zookeeper changed. The changed data:" + event.getData());
				}
				TreeCacheEvent.Type type = event.getType();
				EventType eventType = null;
				String content = null;
				String path = null;
				switch (type) {
				case NODE_ADDED:
					eventType = EventType.NodeCreated;
					path = event.getData().getPath();
					content = event.getData().getData() == null ? "" : new String(event.getData().getData(), CHARSET);
					break;
				case NODE_UPDATED:
					eventType = EventType.NodeDataChanged;
					path = event.getData().getPath();
					content = event.getData().getData() == null ? "" : new String(event.getData().getData(), CHARSET);
					break;
				case NODE_REMOVED:
					path = event.getData().getPath();
					eventType = EventType.NodeDeleted;
					break;
				case INITIALIZED:
					eventType = EventType.INITIALIZED;
					break;
				case CONNECTION_LOST:
					eventType = EventType.CONNECTION_LOST;
					break;
				case CONNECTION_RECONNECTED:
					eventType = EventType.CONNECTION_RECONNECTED;
					break;
				case CONNECTION_SUSPENDED:
					eventType = EventType.CONNECTION_SUSPENDED;
					break;

				}
				dataListener.dataChanged(path, content, eventType);
			}
		}
	}

	@Override
	public String get(String path) {
		if (!checkExists(path)) {
			return null;
		}
		try {
			byte[] dataBytes = client.getData().forPath(path);
			return (dataBytes == null || dataBytes.length == 0) ? null : new String(dataBytes, CHARSET);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void create(String path, boolean ephemeral) {
		if (ephemeral) {
			createEphemeral(path);
		} else {
			createPersistent(path);
		}
	}

	@Override
	public void create(String path, String content, boolean ephemeral) {
		if (ephemeral) {
			createEphemeral(path, content);
		} else {
			createPersistent(path, content);
		}
	}

	protected void createEphemeral(String path) {
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	protected void createEphemeral(String path, String data) {
		byte[] dataBytes = data.getBytes(CHARSET);
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, dataBytes);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	protected void createPersistent(String path) {
		try {
			client.create().creatingParentsIfNeeded().forPath(path);
		} catch (NodeExistsException e) {
			logger.warn("ZNode " + path + " already exists.", e);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	protected void createPersistent(String path, String data) {
		byte[] dataBytes = data.getBytes(CHARSET);
		try {
			client.create().creatingParentsIfNeeded().forPath(path, dataBytes);
		} catch (NodeExistsException e) {
			try {
				client.setData().forPath(path, dataBytes);
			} catch (Exception e1) {
				throw new IllegalStateException(e.getMessage(), e1);
			}
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public List<String> getChildren(String path) {
		try {
			return client.getChildren().forPath(path);
		} catch (NoNodeException e) {
			return null;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public boolean checkExists(String path) {
		try {
			if (client.checkExists().forPath(path) != null) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public void delete(String path) {
		try {
			client.delete().deletingChildrenIfNeeded().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public CompletableFuture<Boolean> asyncCheckExists(String path) {
		CompletableFuture<Boolean> result = new CompletableFuture<Boolean>();
		this.asyncClient.checkExists().forPath(path).whenComplete((r, e) -> {
			if (e != null) {
				result.completeExceptionally(e);
			} else {
				result.complete(r != null);
			}
		});
		return result;
	}

	@Override
	public CompletableFuture<String> asyncGet(String path) {
		return asyncCheckExists(path).thenCompose(res -> {
			if (res) {
				return this._asyncGet(path);
			}
			return CompletableFuture.completedFuture(null);
		});
	}

	private CompletableFuture<String> _asyncGet(String path) {
		CompletableFuture<String> result = new CompletableFuture<>();
		this.asyncClient.getData().forPath(path).whenComplete((bytes, e) -> {
			if (e != null) {
				result.completeExceptionally(e);
			} else {
				result.complete((bytes == null || bytes.length == 0) ? null : new String(bytes, CHARSET));
			}
		});
		return result;
	}

	@Override
	public CompletableFuture<Void> asyncCreate(String path, boolean ephemeral) {
		if (ephemeral) {
			return this.asyncCreateEphemeral(path);
		}
		return this.asyncCreatePersistent(path);
	}

	protected CompletableFuture<Void> asyncCreateEphemeral(String path) {
		CompletableFuture<Void> result = new CompletableFuture<>();
		this.asyncClient.create().withOptions(Sets.newHashSet(CreateOption.createParentsIfNeeded), CreateMode.EPHEMERAL)
				.forPath(path).whenComplete((r, e) -> {
					if (e != null) {
						result.completeExceptionally(e);
					} else {
						result.complete(null);
					}
				});
		return result;
	}

	protected CompletableFuture<Void> asyncCreateEphemeral(String path, String data) {
		byte[] dataBytes = data.getBytes(CHARSET);
		CompletableFuture<Void> result = new CompletableFuture<>();
		this.asyncClient.create().withOptions(Sets.newHashSet(CreateOption.createParentsIfNeeded), CreateMode.EPHEMERAL)
				.forPath(path, dataBytes).whenComplete((r, e) -> {
					if (e != null) {
						result.completeExceptionally(e);
					} else {
						result.complete(null);
					}
				});
		return result;
	}

	protected CompletableFuture<Void> asyncCreatePersistent(String path) {
		CompletableFuture<Void> result = new CompletableFuture<>();
		this.asyncClient.create().withOptions(Sets.newHashSet(CreateOption.createParentsIfNeeded)).forPath(path)
				.whenComplete((r, e) -> {
					if (e != null) {
						result.completeExceptionally(e);
					} else {
						result.complete(null);
					}
				});
		return result;
	}

	protected CompletableFuture<Void> asyncCreatePersistent(String path, String data) {
		byte[] dataBytes = data.getBytes(CHARSET);
		CompletableFuture<Void> result = new CompletableFuture<>();
		this.asyncClient.create().withOptions(Sets.newHashSet(CreateOption.createParentsIfNeeded))
				.forPath(path, dataBytes).whenComplete((r, e) -> {
					if (e != null) {
						result.completeExceptionally(e);
					} else {
						result.complete(null);
					}
				});
		return result;
	}

	@Override
	public CompletableFuture<Void> asyncCreate(String path, String content, boolean ephemeral) {
		if (ephemeral) {
			return this.asyncCreateEphemeral(path, content);
		}
		return this.asyncCreatePersistent(path, content);
	}

	@Override
	public CompletableFuture<List<String>> asyncGetChildren(String path) {
		CompletableFuture<List<String>> result = new CompletableFuture<>();
		this.asyncClient.getChildren().forPath(path).whenComplete((r, e) -> {
			if (e != null) {
				result.completeExceptionally(e);
			} else {
				result.complete(r);
			}
		});
		return result;
	}

	@Override
	public CompletableFuture<Void> asyncDelete(String path) {
		CompletableFuture<Void> result = new CompletableFuture<>();
		this.asyncClient.delete().withOptions(Sets.newHashSet(DeleteOption.deletingChildrenIfNeeded)).forPath(path)
				.whenComplete((r, e) -> {
					if (e != null) {
						result.completeExceptionally(e);
					} else {
						result.complete(null);
					}
				});
		return result;
	}
}
