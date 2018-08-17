package com.swak.vertx.transport;

import java.util.List;
import java.util.Set;

import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.RequestMethod;
import com.swak.vertx.config.AnnotationBean;
import com.swak.vertx.config.RouterBean;
import com.swak.vertx.config.ServiceBean;
import com.swak.vertx.properties.VertxProperties;
import com.swak.vertx.router.HandlerAdapter;
import com.swak.vertx.service.ServiceVerticle;
import com.swak.vertx.utils.Lifecycle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;

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
		super.start(startFuture);

		List<Future> futures = Lists.newArrayList();
		
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
					route = Lifecycle.router.patchWithRegex(path);
				} else {
					route = Lifecycle.router.patch(path);
				}
				if (rb.getRequestMethod() == RequestMethod.GET) {
					route.method(HttpMethod.GET);
				} else if (rb.getRequestMethod() == RequestMethod.POST) {
					route.method(HttpMethod.POST);
				}

				// 添加一个匿名的处理器
				route.handler(ctx -> {
					handlerAdapter.handle(ctx, rb.getHandler());
				});
			}
		}
		List<Future<String>> futures = Lists.newArrayList();
		futures.add(Future.<String>future(s -> {
			annotation.getVertx().deployVerticle(
					new HttpServerVerticle(annotation.getVertx(), annotation.getRouter(), properties.getPort()),
					new DeploymentOptions(), s);
		}));
		return futures;
	}

	/**
	 * 发布组件服务
	 * 
	 * @return
	 */
	private List<Future<String>> startServices() {
		List<Future<String>> futures = Lists.newArrayList();
		Set<ServiceBean> services = annotation.getServices();
		for (ServiceBean service : services) {
			futures.add(Future.<String>future(s -> {
				annotation.getVertx().deployVerticle(new ServiceVerticle(service.getService()), new DeploymentOptions(),
						s);
			}));
		}
		return futures;
	}
}