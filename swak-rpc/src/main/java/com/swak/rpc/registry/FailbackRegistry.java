package com.swak.rpc.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.executor.NamedThreadFactory;
import com.swak.rpc.api.Constants;
import com.swak.rpc.api.URL;
import com.swak.utils.ConcurrentHashSet;

/**
 * 提供失败重试的功能
 * 
 * @author lifeng
 */
public abstract class FailbackRegistry implements Registry {
	
	protected static Logger logger = LoggerFactory.getLogger(FailbackRegistry.class); 

	// Scheduled executor service
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("SwakRegistryFailedRetryTimer", true));
    private final ScheduledFuture<?> retryFuture;
    
	private final Set<URL> failedRegistered = new ConcurrentHashSet<URL>();
	private final Set<URL> failedUnregistered = new ConcurrentHashSet<URL>();
	private final Set<URL> registered = new ConcurrentHashSet<URL>();
	private final ConcurrentMap<String, Set<NotifyListener>> subscribed = new ConcurrentHashMap<String, Set<NotifyListener>>();
    private final ConcurrentMap<String, Set<NotifyListener>> failedSubscribed = new ConcurrentHashMap<String, Set<NotifyListener>>();
    private final ConcurrentMap<String, Set<NotifyListener>> failedUnsubscribed = new ConcurrentHashMap<String, Set<NotifyListener>>();
    private final ConcurrentMap<String, Map<NotifyListener, List<URL>>> failedNotified = new ConcurrentHashMap<String, Map<NotifyListener, List<URL>>>();
    private final ConcurrentMap<String, List<URL>> notified = new ConcurrentHashMap<String, List<URL>>();
    
    public FailbackRegistry () {
    	int retryPeriod = Constants.DEFAULT_REGISTRY_RETRY_PERIOD;
        this.retryFuture = retryExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
            	retry();
            }
        }, retryPeriod, retryPeriod, TimeUnit.MILLISECONDS);
    }
    
	/**
	 * 注册服务
	 */
	@Override
	public void register(URL url) {
		registered.add(url);
		failedRegistered.remove(url);
		failedUnregistered.remove(url);
		doRegister(url).whenComplete((s, t) -> {
			if (!s || t != null) {
				failedRegistered.add(url);
			}
		});
	}

	/**
	 * 取消注册服务
	 */
	@Override
	public void unregister(URL url) {
		registered.remove(url);
		failedRegistered.remove(url);
		failedUnregistered.remove(url);
		doUnregister(url).whenComplete((s, t) -> {
			if (!s || t != null) {
				failedUnregistered.add(url);
			}
		});
	}

	/**
	 * 订阅 url 的改变
	 */
	@Override
	public void subscribe(URL url, NotifyListener listener) {
		String serviceKey = getServiceKey(url);
		Set<NotifyListener> listeners = subscribed.get(serviceKey);
        if (listeners == null) {
            subscribed.putIfAbsent(serviceKey, new ConcurrentHashSet<NotifyListener>());
            listeners = subscribed.get(serviceKey);
        }
        listeners.add(listener);
        removeFailedSubscribed(url, listener);
        doSubscribe(getServiceKey(url), listener).whenComplete((s, t) -> {
			if (t != null) {
				addFailedSubscribed(getServiceKey(url), listener);
			}
		});
	}
	
    private void addFailedSubscribed(String serviceKey, NotifyListener listener) {
        Set<NotifyListener> listeners = failedSubscribed.get(serviceKey);
        if (listeners == null) {
            failedSubscribed.putIfAbsent(serviceKey, new ConcurrentHashSet<NotifyListener>());
            listeners = failedSubscribed.get(serviceKey);
        }
        listeners.add(listener);
    }

    private void removeFailedSubscribed(URL url, NotifyListener listener) {
    	String serviceKey = getServiceKey(url);
        Set<NotifyListener> listeners = failedSubscribed.get(serviceKey);
        if (listeners != null) {
            listeners.remove(listener);
        }
        listeners = failedUnsubscribed.get(serviceKey);
        if (listeners != null) {
            listeners.remove(listener);
        }
        Map<NotifyListener, List<URL>> notified = failedNotified.get(serviceKey);
        if (notified != null) {
            notified.remove(listener);
        }
    }
    
    protected void notify(String serviceKey, NotifyListener listener, List<URL> urls) {
    	notified.put(serviceKey, urls);
    	listener.notify(urls);
    }
    
    /**
     * 取消订阅
     */
    @Override
    public void unsubscribe(URL url, NotifyListener listener) {
    	Set<NotifyListener> listeners = subscribed.get(getServiceKey(url));
        if (listeners != null) {
            listeners.remove(listener);
        }
        removeFailedSubscribed(url, listener);
    }
    
    /**
     * 查找地址
     */
    @Override
    public List<URL> lookup(URL url)  {
    	List<URL> results = getNotified().get(getServiceKey(url));
    	if (results == null) {
    		final AtomicReference<List<URL>> reference = new AtomicReference<List<URL>>();
            NotifyListener listener = new NotifyListener() {
                public void notify(List<URL> urls) {
                    reference.set(urls);
                }
            };
            this.subscribe(url, listener);
            return reference.get(); // 有问题，订阅是异步操作的
    	} else {
    		return results;
    	}
    }
    
    public void destroy() {
    	Set<URL> destroyRegistered = new HashSet<URL>(getRegistered());
        if (!destroyRegistered.isEmpty()) {
            for (URL url : new HashSet<URL>(getRegistered())) {
            	try {
                    unregister(url);
                    if (logger.isInfoEnabled()) {
                        logger.info("Destroy unregister url " + url);
                    }
                } catch (Throwable t) {
                    logger.warn("Failed to unregister url " + url + " to registry  on destroy, cause: " + t.getMessage(), t);
                }
            }
        }
        Map<String, Set<NotifyListener>> destroySubscribed = new HashMap<String, Set<NotifyListener>>(getSubscribed());
        if (!destroySubscribed.isEmpty()) {
            for (Map.Entry<String, Set<NotifyListener>> entry : destroySubscribed.entrySet()) {
            	String url = entry.getKey();
                for (NotifyListener listener : entry.getValue()) {
                    try {
                        unsubscribe(URL.valueOf(url), listener);
                        if (logger.isInfoEnabled()) {
                            logger.info("Destroy unsubscribe url " + url);
                        }
                    } catch (Throwable t) {
                        logger.warn("Failed to unsubscribe url " + url + " to registry  on destroy, cause: " + t.getMessage(), t);
                    }
                }
            }
        }
        try {
            retryFuture.cancel(true);
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }
    
    // ==== Service Key ====
    protected String getServiceKey(URL url) {
    	return url.getSequence();
    }
	// ==== Template method ====
	protected abstract CompletionStage<Boolean> doRegister(URL url);
	protected abstract CompletionStage<Boolean> doUnregister(URL url);
	protected abstract CompletionStage<Void> doSubscribe(String serviceKey, NotifyListener listener);
	protected abstract CompletionStage<Void> doUnsubscribe(String serviceKey, NotifyListener listener);
	// ==== get ====
	public Set<URL> getRegistered() {
		return registered;
	}
	public ConcurrentMap<String, Set<NotifyListener>> getSubscribed() {
		return subscribed;
	}
	public ConcurrentMap<String, List<URL>> getNotified() {
		return notified;
	}
	
    // Retry the failed actions
    protected void retry() {
        if (!failedRegistered.isEmpty()) {
            Set<URL> failed = new HashSet<URL>(failedRegistered);
            if (failed.size() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Retry register " + failed);
                }
                try {
                    for (URL url : failed) {
                        try {
                            doRegister(url);
                            failedRegistered.remove(url);
                        } catch (Throwable t) { // Ignore all the exceptions and wait for the next retry
                            logger.warn("Failed to retry register " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                        }
                    }
                } catch (Throwable t) { // Ignore all the exceptions and wait for the next retry
                    logger.warn("Failed to retry register " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                }
            }
        }
        if (!failedUnregistered.isEmpty()) {
            Set<URL> failed = new HashSet<URL>(failedUnregistered);
            if (failed.size() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Retry unregister " + failed);
                }
                try {
                    for (URL url : failed) {
                        try {
                            doUnregister(url);
                            failedUnregistered.remove(url);
                        } catch (Throwable t) { // Ignore all the exceptions and wait for the next retry
                            logger.warn("Failed to retry unregister  " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                        }
                    }
                } catch (Throwable t) { // Ignore all the exceptions and wait for the next retry
                    logger.warn("Failed to retry unregister  " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                }
            }
        }
        if (!failedSubscribed.isEmpty()) {
            Map<String, Set<NotifyListener>> failed = new HashMap<String, Set<NotifyListener>>(failedSubscribed);
            for (Map.Entry<String, Set<NotifyListener>> entry : new HashMap<String, Set<NotifyListener>>(failed).entrySet()) {
                if (entry.getValue() == null || entry.getValue().size() == 0) {
                    failed.remove(entry.getKey());
                }
            }
            if (failed.size() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Retry subscribe " + failed);
                }
                try {
                    for (Map.Entry<String, Set<NotifyListener>> entry : failed.entrySet()) {
                    	String url = entry.getKey();
                        Set<NotifyListener> listeners = entry.getValue();
                        for (NotifyListener listener : listeners) {
                            try {
                                doSubscribe(url, listener);
                                listeners.remove(listener);
                            } catch (Throwable t) { // Ignore all the exceptions and wait for the next retry
                                logger.warn("Failed to retry subscribe " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                            }
                        }
                    }
                } catch (Throwable t) { // Ignore all the exceptions and wait for the next retry
                    logger.warn("Failed to retry subscribe " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                }
            }
        }
        if (!failedUnsubscribed.isEmpty()) {
            Map<String, Set<NotifyListener>> failed = new HashMap<String, Set<NotifyListener>>(failedUnsubscribed);
            for (Map.Entry<String, Set<NotifyListener>> entry : new HashMap<String, Set<NotifyListener>>(failed).entrySet()) {
                if (entry.getValue() == null || entry.getValue().size() == 0) {
                    failed.remove(entry.getKey());
                }
            }
            if (failed.size() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Retry unsubscribe " + failed);
                }
                try {
                    for (Map.Entry<String, Set<NotifyListener>> entry : failed.entrySet()) {
                    	String url = entry.getKey();
                        Set<NotifyListener> listeners = entry.getValue();
                        for (NotifyListener listener : listeners) {
                            try {
                                doUnsubscribe(url, listener);
                                listeners.remove(listener);
                            } catch (Throwable t) { // Ignore all the exceptions and wait for the next retry
                                logger.warn("Failed to retry unsubscribe " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                            }
                        }
                    }
                } catch (Throwable t) { // Ignore all the exceptions and wait for the next retry
                    logger.warn("Failed to retry unsubscribe " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                }
            }
        }
        if (!failedNotified.isEmpty()) {
            Map<String, Map<NotifyListener, List<URL>>> failed = new HashMap<String, Map<NotifyListener, List<URL>>>(failedNotified);
            for (Map.Entry<String, Map<NotifyListener, List<URL>>> entry : new HashMap<String, Map<NotifyListener, List<URL>>>(failed).entrySet()) {
                if (entry.getValue() == null || entry.getValue().size() == 0) {
                    failed.remove(entry.getKey());
                }
            }
            if (failed.size() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Retry notify " + failed);
                }
                try {
                    for (Map<NotifyListener, List<URL>> values : failed.values()) {
                        for (Map.Entry<NotifyListener, List<URL>> entry : values.entrySet()) {
                            try {
                                NotifyListener listener = entry.getKey();
                                List<URL> urls = entry.getValue();
                                listener.notify(urls);
                                values.remove(listener);
                            } catch (Throwable t) { // Ignore all the exceptions and wait for the next retry
                                logger.warn("Failed to retry notify " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                            }
                        }
                    }
                } catch (Throwable t) { // Ignore all the exceptions and wait for the next retry
                    logger.warn("Failed to retry notify " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                }
            }
        }
    }
}