package com.swak.vertx.transport;

import java.util.List;
import java.util.Set;

import org.springframework.aop.support.AopUtils;

import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.vertx.config.AnnotationBean;
import com.swak.vertx.config.ServiceBean;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.RouterHandler;
import com.swak.vertx.transport.codec.Msg;
import com.swak.vertx.transport.codec.MsgCodec;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

/**
 * 响应式服务的启动，对于 vertx 来说， 所有的 Verticle 都在这个服务中发布
 * 
 * @author lifeng
 */
public class MainVerticle extends AbstractVerticle {

	private final AnnotationBean annotation;
	private final VertxProperties properties;

	public MainVerticle(AnnotationBean annotation, VertxProperties properties) {
		this.annotation = annotation;
		this.properties = properties;
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
	 * 启动服务, startFuture.complete 底层也没有修改，暂时不知道修改方案
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public void start(Future<Void> startFuture) throws Exception {
		List<Future> futures = Lists.newArrayList();

		// 自定义一些配置
		this.customConfig();

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
	 * 发布组件服务
	 * 
	 * @return
	 */
	private List<Future<String>> startServices() {
		// 开始发布
		List<Future<String>> futures = Lists.newArrayList();
		Set<ServiceBean> services = annotation.getServices();
		for (ServiceBean service : services) {
			if (service.isHttp()) {
				futures.addAll(this.startHttp(service));
			} else {
				futures.addAll(this.startService(service));
			}
		}
		return futures;
	}

	// 发布为Http 服务
	private List<Future<String>> startHttp(ServiceBean service) {

		// Router 处理器
		RouterHandler routerHandler = this.getProxyService(service);

		// 初始化 router
		routerHandler.initRouter(vertx, annotation);

		// 启动监听服务
		List<Future<String>> futures = Lists.newArrayList();

		// 以EventLoop 的方式发布
		DeploymentOptions options = new DeploymentOptions().setWorker(false);
		int intstances = getDeploymentIntstances(service);
		for (int i = 1; i <= intstances; i++) {
			futures.add(Future.<String>future(s -> {
				vertx.deployVerticle(new HttpVerticle(routerHandler, properties.getHost(), properties.getPort()),
						options, s);
			}));
		}
		return futures;
	}

	// 发布成Tcp 服务
	private List<Future<String>> startService(ServiceBean service) {

		// 发布服务标示
		List<Future<String>> futures = Lists.newArrayList();

		// 以worker 的方式发布
		DeploymentOptions options = new DeploymentOptions().setWorker(true);

		// 设置了运行的线程池(如果没有配置则，默认只有一个)
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
			futures.add(Future.<String>future(s -> {
				vertx.deployVerticle(new ServiceVerticle(this.getProxyService(service), service.getServiceType()),
						options, s);
			}));
		}
		return futures;
	}

	/**
	 * 获得发布个数
	 * 
	 * @return
	 */
	private int getDeploymentIntstances(ServiceBean service) {
		int intstances = service.getInstances();
		if (intstances <= 0) {
			intstances = properties.getEventLoopPoolSize();
		}
		return intstances;
	}

	/**
	 * 获得代理服务
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T getProxyService(ServiceBean service) {
		Object proxy = service.getService();
		if (!AopUtils.isAopProxy(proxy)) {
			return (T) annotation.getProxy(service.getService());
		}
		return (T) proxy;
	}
}