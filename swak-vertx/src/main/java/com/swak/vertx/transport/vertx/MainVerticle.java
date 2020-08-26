package com.swak.vertx.transport.vertx;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.Constants;
import com.swak.OS;
import com.swak.annotation.Context;
import com.swak.annotation.Server;
import com.swak.reactivex.context.EndPoints;
import com.swak.reactivex.context.EndPoints.EndPoint;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.vertx.config.RouterConfig;
import com.swak.vertx.config.ImBean;
import com.swak.vertx.config.RouterBean;
import com.swak.vertx.config.ServiceBean;
import com.swak.vertx.config.VertxConfigs;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.protocol.http.ErrorHandler;
import com.swak.vertx.protocol.im.ImRouter;
import com.swak.vertx.protocol.im.ImRouter.ImRoute;
import com.swak.vertx.transport.ServerVerticle;
import com.swak.vertx.transport.codec.Msg;
import com.swak.vertx.transport.codec.MsgCodec;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * 响应式服务的启动，对于 vertx 来说， 所有的 Verticle 都在这个服务中发布
 *
 * @author: lifeng
 * @date: 2020/3/29 21:17
 */
public class MainVerticle extends AbstractVerticle implements ServerVerticle {

	protected Logger routerLogger = LoggerFactory.getLogger(ServerVerticle.class);

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

		// 启动Http组件
		futures.addAll(this.startHttps());

		// 启动WebSocket组件
		futures.addAll(this.startWebSockets());

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

	@SuppressWarnings({ "unchecked" })
	private List<Future<EndPoint>> startServices() {
		List<Future<EndPoint>> futures = Lists.newArrayList();
		Set<ServiceBean> services = VertxConfigs.me().getServices();
		for (ServiceBean service : services) {
			futures.add(this.startService(service));
		}
		return futures;
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	private Future startService(ServiceBean service) {

		// 发布服务标示
		List<Future> futures = Lists.newArrayList();

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
		int intstances = getDeploymentIntstances(service.getInstances());
		for (int i = 1; i <= intstances; i++) {
			Future<String> stFuture = Future.future(
					s -> vertx.deployVerticle(new ServiceVerticle(service.getRef(), service.getType()), options, s));
			futures.add(stFuture.map(id -> null));
		}
		// 合并成一个结果
		return CompositeFuture.all(futures).map(res -> {
			return null;
		});
	}

	private List<Future<EndPoint>> startHttps() {

		List<Future<EndPoint>> futures = Lists.newArrayList();
		Map<Integer, List<RouterBean>> routers = VertxConfigs.me().getRouters();

		// 发布成多个Http服务
		routers.keySet().forEach(port -> {
			futures.add(this.startHttp(port, routers.get(port)));
		});

		return futures;
	}

	@SuppressWarnings("rawtypes")
	private Future<EndPoint> startHttp(int port, List<RouterBean> routers) {

		// 发布的 Host、Port
		String deployHost = properties.getHost();
		int deployPort = port <= 0 ? properties.getPort() : port;

		// 获得路由 -- Router 是线程安全的所以多个Verticle实例可以公用
		Router router = this.getRouter(routers);

		// 服务器配置
		HttpServerOptions httpServerOptions = this.httpServerOptions(properties);

		// 启动监听服务
		List<Future> futures = Lists.newArrayList();

		// 以EventLoop 的方式发布
		DeploymentOptions options = new DeploymentOptions().setWorker(false);
		int intstances = getDeploymentIntstances(-1);
		for (int i = 1; i <= intstances; i++) {
			Future<String> stFuture = Future.future(s -> vertx.deployVerticle(
					new HttpServerVerticle(router, httpServerOptions, deployHost, deployPort), options, s));
			futures.add(stFuture);
		}

		// 合并成一个结果
		return CompositeFuture.all(futures).map(res -> {
			return new EndPoint().setScheme(Server.Http).setHost(this.endPoints.getHost()).setPort(deployPort)
					.setParallel(intstances);
		});
	}

	private Router getRouter(List<RouterBean> routers) {

		// 初始化
		Router router = Router.router(vertx);
		router.errorHandler(404, new ErrorHandler(404));
		router.errorHandler(500, new ErrorHandler(500));

		// 路由基本配置
		Set<RouterConfig> configs = VertxConfigs.me().getRouterConfigs();
		for (RouterConfig config : configs) {
			config.apply(vertx, router);
		}

		// 单个路由定义
		for (RouterBean rb : routers) {
			rb.mounton(router);
		}

		// 打印路由信息
		if (routerLogger.isDebugEnabled()) {
			List<Route> routes = router.getRoutes();
			for (Route route : routes) {
				routerLogger.debug("{}\t{}", route.methods() != null ? route.methods().toString() : "All",
						route.getPath() != null ? route.getPath() : "All");
			}
		}
		return router;
	}

	private List<Future<EndPoint>> startWebSockets() {
		List<Future<EndPoint>> futures = Lists.newArrayList();
		Map<Integer, List<ImBean>> routers = VertxConfigs.me().getWebSockets();

		// 发布成多个Http服务
		routers.keySet().forEach(port -> {
			futures.add(this.startWebSocket(port, routers.get(port)));
		});

		return futures;
	}

	@SuppressWarnings("rawtypes")
	private Future<EndPoint> startWebSocket(int port, List<ImBean> routers) {

		// 发布的 Host、Port
		String deployHost = properties.getHost();
		int deployPort = port <= 0 ? properties.getWebSocketPort() : port;

		// 处理器
		ImRouter imRouter = this.getImRouter(routers);

		// 服务器配置
		HttpServerOptions httpServerOptions = this.httpServerOptions(properties);

		// 启动监听服务
		List<Future> futures = Lists.newArrayList();

		// 以EventLoop 的方式发布
		DeploymentOptions options = new DeploymentOptions().setWorker(false);
		int intstances = getDeploymentIntstances(-1);
		for (int i = 1; i <= intstances; i++) {
			Future<String> stFuture = Future.future(s -> vertx.deployVerticle(
					new ImServerVerticle(imRouter, httpServerOptions, deployHost, deployPort), options, s));
			futures.add(stFuture);
		}

		// 合并成一个结果
		return CompositeFuture.all(futures).map(res -> {
			return new EndPoint().setScheme(Server.Ws).setHost(this.endPoints.getHost()).setPort(properties.getPort())
					.setParallel(intstances);
		});
	}

	private ImRouter getImRouter(List<ImBean> routers) {

		// Im 的 Router
		ImRouter imRouter = new ImRouter();

		// 单个路由定义
		for (ImBean rb : routers) {
			rb.mounton(imRouter);
		}

		// 打印路由信息
		if (routerLogger.isDebugEnabled()) {
			List<ImRoute> routes = imRouter.getRoutes();
			for (ImRoute route : routes) {
				routerLogger.debug("{}", route.getOps() != null ? route.getOps().toString() : "All");
			}
		}
		return imRouter;
	}

	private int getDeploymentIntstances(int intstances) {
		if (intstances <= 0) {
			intstances = properties.getEventLoopPoolSize();
		}
		return intstances;
	}
}