package com.swak.flux.verticle;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.swak.closable.ShutDownHook;
import com.swak.flux.transport.http.server.HttpServerProperties;
import com.swak.reactivex.transport.resources.EventLoopFactory;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

/**
 * 
 * 简单的异步执行框架, 属于http 服务的一部分
 * 
 * @author lifeng
 */
public class FluxImpl implements Flux {

	private final HttpServerProperties properties;
	private ExecutorService workerPool;
	private Map<String, ExecutorService> shardWorkPools;
	private Map<String, Deployment> deployments;

	public FluxImpl(HttpServerProperties properties) {
		this.properties = properties;
		shardWorkPools = Maps.newHashMap();
		deployments = Maps.newHashMap();
		workerPool = Executors.newFixedThreadPool(properties.getWorkerThreads(),
				new EventLoopFactory(false, "Flux.worker-", new AtomicLong()));
		shutDownHook();
	}

	private void shutDownHook() {
		ShutDownHook.registerShutdownHook(() -> {
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
	public CompletableFuture<Msg> sendMessage(String address, Msg request, int timeout) {
		CompletableFuture<Msg> future;
		Deployment deployment = lookupDeployment(address);
		if (deployment != null) {
			future = CompletableFuture.supplyAsync(() -> {
				return deployment.getVerticle().handle(request);
			}, deployment.getExecutor());
		} else {
			request.reset().setError("address not found");
			future = CompletableFuture.completedFuture(request);
		}
		return future;
	}

	private Deployment lookupDeployment(String address) {
		return deployments.get(address);
	}

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

	/**
	 * 发布
	 * 
	 * @author lifeng
	 */
	public static class Deployment implements Serializable {

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
}
