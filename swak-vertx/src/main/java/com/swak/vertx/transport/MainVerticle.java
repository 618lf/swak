package com.swak.vertx.transport;

import java.util.List;
import java.util.Set;

import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.RequestMethod;
import com.swak.vertx.config.AnnotationBean;
import com.swak.vertx.config.IRouterSupplier;
import com.swak.vertx.config.RouterBean;
import com.swak.vertx.config.ServiceBean;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.HandlerAdapter;
import com.swak.vertx.handler.ServiceHandler;
import com.swak.vertx.handler.codec.Msg;
import com.swak.vertx.handler.codec.MsgCodec;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * 响应式服务的启动，对于 vertx 来说， 所有的 Verticle 都在这个服务中发布
 * 
 * @author lifeng
 */
public class MainVerticle extends AbstractVerticle {

	private final AnnotationBean annotation;
	private final VertxProperties properties;
	private final HandlerAdapter handlerAdapter;

	public MainVerticle(AnnotationBean annotation, HandlerAdapter handlerAdapter, VertxProperties properties) {
		this.annotation = annotation;
		this.properties = properties;
		this.handlerAdapter = handlerAdapter;
	}

	/**
	 * 显示启动的服务
	 * 
	 * @return
	 */
	public String getAddresses() {
		return String.valueOf(properties.getPort());
	}

	/**
	 * 启动服务
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void start(Future<Void> startFuture) throws Exception {
		List<Future> futures = Lists.newArrayList();

		// 自定义一些配置
		this.customConfig();

		// 启动http服务器
		futures.addAll(this.startHttpServer());

		// 启动服务组件
		futures.addAll(this.startServices());

		CompositeFuture.all(futures).setHandler(res -> {
			if (res.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(res.cause());
			}
		});
	}

	private void customConfig() {
		vertx.eventBus().registerDefaultCodec(Msg.class, new MsgCodec());
	}

	/**
	 * 发布路由服务
	 * 
	 * @return
	 */
	private List<Future<String>> startHttpServer() {
		Set<RouterBean> routers = annotation.getRouters();
		for (RouterBean rb : routers) {
			Set<String> paths = rb.getPatterns();
			for (String path : paths) {
				Route route = null;
				if (StringUtils.contains(path, "*")) {
					route = annotation.getRouter().patchWithRegex(path);
				} else {
					route = annotation.getRouter().patch(path);
				}
				if (rb.getRequestMethod() == RequestMethod.POST) {
					route.method(HttpMethod.POST);
				} else {
					route.method(HttpMethod.GET);
				}

				// 添加一个匿名的处理器
				route.handler(ctx -> {
					handlerAdapter.handle(ctx, rb.getHandler());
				});
			}
		}
		Set<IRouterSupplier> rses = annotation.getRouterSuppliers();
		for (IRouterSupplier rs : rses) {
			Router router = rs.get(vertx);
			if (router != null) {
				annotation.getRouter().mountSubRouter(rs.path(), router);
			}
		}

		// 启动 eventloop 数量的 http 处理器
		DeploymentOptions options = new DeploymentOptions();
		List<Future<String>> futures = Lists.newArrayList();
		for (int i = 1; i <= properties.getEventLoopPoolSize(); i++) {
			futures.add(Future.<String>future(s -> {
				vertx.deployVerticle(new HttpServerVerticle(vertx, annotation.getRouter(), properties.getPort()),
						options, s);
			}));
		}
		return futures;
	}

	/**
	 * 发布组件服务
	 * 
	 * @return
	 */
	private List<Future<String>> startServices() {
		// 开始发布
		List<Future<String>> futures = Lists.newArrayList();
		Set<ServiceBean> services = annotation.getServices();
		for (ServiceBean service : services) {

			// 以worker 的方式发布
			DeploymentOptions options = new DeploymentOptions().setWorker(true);

			// 设置了运行的线程池
			String usePool = service.getUse_pool();
			Integer poolSize = properties.getWorkers().get(usePool);
			if (StringUtils.isNotBlank(usePool) && poolSize != null && poolSize > 0) {
				options.setWorkerPoolName(HttpConst.workerPrex + usePool);
				options.setWorkerPoolSize(poolSize);
			} else {
				options.setWorkerPoolSize(properties.getWorkerThreads());
			}

			futures.add(Future.<String>future(s -> {
				vertx.deployVerticle(new ServiceHandler(service.getService(), service.getServiceType()), options, s);
			}));
		}
		return futures;
	}
}