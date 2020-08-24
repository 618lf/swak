package com.swak.vertx.transport;

import java.util.List;
import java.util.Set;

import com.swak.Constants;
import com.swak.OS;
import com.swak.annotation.Context;
import com.swak.annotation.Server;
import com.swak.reactivex.context.EndPoints;
import com.swak.reactivex.context.EndPoints.EndPoint;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.vertx.config.ServiceBean;
import com.swak.vertx.config.VertxConfigs;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.protocol.http.RouterHandler;
import com.swak.vertx.transport.codec.Msg;
import com.swak.vertx.transport.codec.MsgCodec;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * 响应式服务的启动，对于 vertx 来说， 所有的 Verticle 都在这个服务中发布
 *
 * @author: lifeng
 * @date: 2020/3/29 21:17
 */
public class MainVerticle extends AbstractVerticle {

	private final VertxProperties properties;
	private EndPoints endPoints;

	public MainVerticle(VertxProperties properties) {
		this.properties = properties;

		// 服务器地址
		String hostName = properties.getHost();
		if (!Constants.LOCALHOST.equals(hostName)) {
			hostName = OS.ip();
		}
		this.endPoints = new EndPoints().setHost(hostName);
	}

	/**
	 * 服务的端口
	 */
	public EndPoints getEndPoints() {
		return endPoints;
	}

	/**
	 * 异步的启动
	 */
	@Override
	@SuppressWarnings({ "rawtypes" })
	public void start(Promise<Void> startPromise) {
		List<Future> futures = Lists.newArrayList();

		// 自定义一些配置
		vertx.eventBus().registerDefaultCodec(Msg.class, new MsgCodec());

		// 启动服务组件
		futures.addAll(this.startServices());

		// 等待所有服务全部启动完成
		CompositeFuture.all(futures).onComplete(res -> {
			if (res.succeeded()) {
				List<EndPoint> endPoints = Lists.newArrayList();
				res.result().list().forEach(endpoint -> {
					if (endpoint != null && endpoint instanceof EndPoint) {
						endPoints.add((EndPoint) endpoint);
					}
				});
				this.endPoints.setEndPoints(endPoints);
				startPromise.complete();
			} else {
				startPromise.fail(res.cause());
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private List<Future<EndPoint>> startServices() {
		List<Future<EndPoint>> futures = Lists.newArrayList();
		List<Future> serviceFutures = Lists.newArrayList();
		Set<ServiceBean> services = VertxConfigs.me().getServices();
		for (ServiceBean service : services) {
			if (service.getServer() == Server.Http) {
				futures.add(this.startHttp(service));
			} else if (service.getServer() == Server.IM) {
				futures.add(this.startWebSocket(service));
			} else {
				serviceFutures.addAll(this.startService(service));
			}
		}
		futures.add(CompositeFuture.all(serviceFutures).map(res -> {
			return null;
		}));
		return futures;
	}

	@SuppressWarnings("rawtypes")
	private Future<EndPoint> startWebSocket(ServiceBean service) {

		// 启动监听服务
		List<Future> futures = Lists.newArrayList();

		// 以EventLoop 的方式发布
		DeploymentOptions options = new DeploymentOptions().setWorker(false);
		int intstances = getDeploymentIntstances(service);
		for (int i = 1; i <= intstances; i++) {
			Future<String> stFuture = Future.future(s -> vertx.deployVerticle(
					new ImServerVerticle(this.getService(service), service.getType(), properties), options, s));
			futures.add(stFuture);
		}

		// 合并成一个结果
		return CompositeFuture.all(futures).map(res -> {
			return new EndPoint().setScheme(Server.IM).setHost(this.endPoints.getHost()).setPort(properties.getPort())
					.setParallel(intstances);
		});
	}

	@SuppressWarnings("rawtypes")
	private Future<EndPoint> startHttp(ServiceBean service) {

		// Router 处理器
		RouterHandler routerHandler = this.getService(service);

		// 初始化 router
		routerHandler.getRouter();

		// 启动监听服务
		List<Future> futures = Lists.newArrayList();

		// 以EventLoop 的方式发布
		DeploymentOptions options = new DeploymentOptions().setWorker(false);
		int intstances = getDeploymentIntstances(service);
		for (int i = 1; i <= intstances; i++) {
			Future<String> stFuture = Future
					.future(s -> vertx.deployVerticle(new HttpServerVerticle(routerHandler, properties), options, s));
			futures.add(stFuture);
		}

		// 合并成一个结果
		return CompositeFuture.all(futures).map(res -> {
			return new EndPoint().setScheme(Server.Http).setHost(this.endPoints.getHost()).setPort(properties.getPort())
					.setParallel(intstances);
		});
	}

	@SuppressWarnings({ "deprecation" })
	private List<Future<EndPoint>> startService(ServiceBean service) {

		// 发布服务标示
		List<Future<EndPoint>> futures = Lists.newArrayList();

		// 以worker 的方式发布 默认是并发的执行代码。
		DeploymentOptions options = new DeploymentOptions().setWorker(true);

		// 运行的线程模型
		if (service.getContext() == Context.IO) {
			options.setWorker(false);
		} else if (service.getContext() == Context.Order) {
			options.setWorker(true);
			options.setMultiThreaded(false);
		} else if (service.getContext() == Context.Concurrent) {
			options.setWorker(true);
			options.setMultiThreaded(true);
		}

		// 自定义的线程池
		String usePool = service.getUse_pool();
		Integer poolSize = properties.getWorkers().get(usePool);
		if (StringUtils.isNotBlank(usePool)) {
			options.setWorkerPoolName("vert.x-worker-" + usePool + "-thread");
			options.setWorkerPoolSize(poolSize == null ? 1 : poolSize);
		} else {
			options.setWorkerPoolSize(properties.getWorkerThreads());
		}

		// 配置发布多个服务
		int intstances = getDeploymentIntstances(service);
		for (int i = 1; i <= intstances; i++) {
			Future<String> stFuture = Future.future(s -> vertx
					.deployVerticle(new ServiceVerticle(this.getService(service), service.getType()), options, s));
			futures.add(stFuture.map(id -> null));
		}
		return futures;
	}

	private int getDeploymentIntstances(ServiceBean service) {
		int intstances = service.getInstances();
		if (intstances <= 0) {
			intstances = properties.getEventLoopPoolSize();
		}
		return intstances;
	}

	/**
	 * Bug: 在另外的线程中获取bean 会导致spring 死锁<br>
	 * 所有获取bean的地方放在 init()在服务启动时在获取。
	 * 
	 * @param <T>
	 * @param service
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T getService(ServiceBean service) {
		Object proxy = service.getRef();
		return (T) proxy;
	}
}