package com.swak.vertx.transport;

import com.swak.reactivex.context.Server;
import com.swak.reactivex.context.ServerException;
import com.swak.vertx.config.AnnotationBean;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * 发布服务的一个入口
 * 
 * @author lifeng
 */
public class ReactiveServer implements Server {

	private final MainVerticle mainVerticle;
	private final Vertx vertx;

	public ReactiveServer(AnnotationBean annotationBean, MainVerticle mainVerticle) {
		this.vertx = annotationBean.getVertx();
		this.mainVerticle = mainVerticle;
	}

	@Override
	public void start() throws ServerException {
		Future<Void> startFuture = Future.future();
		vertx.deployVerticle(mainVerticle, res -> {
			startFuture.complete();
		});

		// 应该会阻塞在这里
		startFuture.result();
	}

	@Override
	public void stop() throws ServerException {
		Future<Void> stopFuture = Future.future();
		vertx.undeploy(mainVerticle.deploymentID(), res -> {
			stopFuture.complete();
		});

		// 应该会阻塞在这里
		stopFuture.result();
	}

	/**
	 * 显示启动的服务
	 */
	@Override
	public String getAddresses() {
		return mainVerticle.getAddresses();
	}
}