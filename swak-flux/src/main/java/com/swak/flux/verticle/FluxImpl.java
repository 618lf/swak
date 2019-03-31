package com.swak.flux.verticle;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.closable.ShutDownHook;
import com.swak.flux.transport.server.HttpServerProperties;
import com.swak.reactivex.transport.resources.EventLoopFactory;
import com.swak.utils.ConcurrentHashSet;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

/**
 * 
 * 简单的异步执行框架, 属于http 服务的一部分
 * 
 * @author lifeng
 */
public class FluxImpl implements Flux {
	private Logger logger = LoggerFactory.getLogger(Flux.class);
	private final ConcurrentHashSet<Msg> callbackMap = new ConcurrentHashSet<>();
	private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1,
			new EventLoopFactory(false, "Flux.TimeOut", new AtomicLong()));
	private final HttpServerProperties properties;
	private ExecutorService workerPool;
	private Map<String, ExecutorService> shardWorkPools;
	private Map<String, Deployment> deployments;
	private ScheduledFuture<?> timeMonitorFuture = null;

	public FluxImpl(HttpServerProperties properties) {
		this.properties = properties;
		shardWorkPools = Maps.newHashMap();
		deployments = Maps.newHashMap();
		workerPool = Executors.newFixedThreadPool(properties.getWorkerThreads(),
				new EventLoopFactory(false, "Flux.worker-", new AtomicLong()));
		timeMonitorFuture = scheduledExecutor.scheduleWithFixedDelay(new TimeoutMonitor("timeout_monitor"), 100, 100,
				TimeUnit.MILLISECONDS);
		shutDownHook();
	}

	private void shutDownHook() {
		ShutDownHook.registerShutdownHook(() -> {
			this.close();
			shutDownPool(scheduledExecutor);
			shutDownPool(workerPool);
			shardWorkPools.values().forEach(pool -> {
				shutDownPool(pool);
			});
			deployments.clear();
			shardWorkPools.clear();
		});
	}

	private void shutDownPool(ExecutorService workerPool) {
		try {
			workerPool.shutdown();
			if (workerPool.awaitTermination(30, TimeUnit.SECONDS)) {
				workerPool.shutdownNow();
			}
		} catch (Exception e) {
			workerPool.shutdownNow();
		}
	}

	@Override
	public CompletableFuture<Msg> sendMessage(String address, Msg request) {
		CompletableFuture<Msg> future;
		Deployment deployment = lookupDeployment(address);
		if (deployment != null) {
			future = CompletableFuture.supplyAsync(() -> {
				return deployment.getVerticle().handle(request);
			}, deployment.getExecutor()).thenApply(res -> {
				removeCallback(request);
				return res;
			});
			registerCallback(request.setFuture(future));
		} else {
			request.reset().setError("address not found");
			future = CompletableFuture.completedFuture(request);
		}
		return future;
	}

	/**
	 * 注册回调的, 请求最好设置一个最大的执行时间
	 * 
	 * <pre>
	 * 进行最大的请求并发数的控制，如果超过NETTY_CLIENT_MAX_REQUEST的话，那么throw reject exception
	 * </pre>
	 */
	public void registerCallback(Msg request) {
		if (this.callbackMap.size() >= 20000) {
			throw new RuntimeException("Request over of max concurrent request, drop request");
		}
		this.callbackMap.add(request);
	}

	/**
	 * 删除请求
	 * 
	 * @param request
	 */
	public void removeCallback(Msg request) {
		callbackMap.remove(request);
	}

	/**
	 * 查找发布
	 * 
	 * @param address
	 * @return
	 */
	private Deployment lookupDeployment(String address) {
		return deployments.get(address);
	}

	/**
	 * 创建线程池
	 * 
	 * @param workPoolName
	 * @param workPoolSize
	 * @return
	 */
	private Executor createShardWorkPool(String workPoolName, int workPoolSize) {
		ExecutorService workerExec = Executors.newFixedThreadPool(workPoolSize,
				new EventLoopFactory(false, workPoolName, new AtomicLong()));
		shardWorkPools.put(workPoolName, workerExec);
		return workerExec;
	}

	/**
	 * 发布服务
	 */
	@Override
	public void deployment(Verticle verticle) {
		this.deployment(verticle, null);
	}

	/**
	 * 发布服务
	 */
	@Override
	public void deployment(Verticle verticle, DeploymentOptions options) {
		if (deployments.containsKey(verticle.address())) {
			throw new RuntimeException("Address Repeat" + verticle.address());
		}
		Executor executor = workerPool;
		if (!(options == null || StringUtils.isBlank(options.getWorkPoolName()))) {
			executor = shardWorkPools.get(options.getWorkPoolName());
			if (executor == null) {
				Integer workPoolSize = this.properties.getWorkers() != null
						? this.properties.getWorkers().get(options.getWorkPoolName())
						: 1;
				executor = createShardWorkPool(options.getWorkPoolName(), workPoolSize);
			}
		}
		Deployment deployment = new Deployment(verticle, executor);
		deployments.put(verticle.address(), deployment);
	}

	/**
	 * 开闭资源
	 */
	@Override
	public void close() {
		try {
			timeMonitorFuture.cancel(true);
		} catch (Exception e) {
			logger.error("Close Flux Error {}", e);
		}
	}

	/**
	 * 发布实例
	 * 
	 * @author lifeng
	 */
	class Deployment implements Serializable {

		private static final long serialVersionUID = 1L;
		private final Verticle verticle;
		private final Executor executor;

		public Deployment(Verticle verticle, Executor executor) {
			this.verticle = verticle;
			this.executor = executor;
		}

		public Verticle getVerticle() {
			return verticle;
		}

		public Executor getExecutor() {
			return executor;
		}
	}

	/**
	 * 回收超时任务
	 *
	 * @author maijunsheng
	 */
	class TimeoutMonitor implements Runnable {
		private String name;

		public TimeoutMonitor(String name) {
			this.name = name;
		}

		@Override
		public void run() {
			long currentTime = System.currentTimeMillis();
			callbackMap.forEach(future -> {
				try {
					if (future.getCreatetime() + future.getTimeOut() < currentTime) {
						removeCallback(future);
						future.cancel();
					}
				} catch (Exception e) {
					logger.error(name + " clear timeout future Error {}", e);
				}
			});
		}
	}

	/**
	 * 发布方式
	 * 
	 * @author lifeng
	 */
	public static class DeploymentOptions implements Serializable {
		private static final long serialVersionUID = 1L;
		private String workPoolName;

		public String getWorkPoolName() {
			return workPoolName;
		}

		public void setWorkPoolName(String workPoolName) {
			this.workPoolName = workPoolName;
		}
	}
}
