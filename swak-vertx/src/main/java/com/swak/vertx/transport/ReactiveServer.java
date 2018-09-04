package com.swak.vertx.transport;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.context.Server;
import com.swak.reactivex.context.ServerException;
import com.swak.vertx.config.AnnotationBean;
import com.swak.vertx.config.VertxProperties;

/**
 * 发布服务的一个入口
 * 
 * @author lifeng
 */
public class ReactiveServer implements Server {

	private static Logger Logger = LoggerFactory.getLogger(ReactiveServer.class);
	private final AnnotationBean annotation;
	private final MainVerticle mainVerticle;

	public ReactiveServer(AnnotationBean annotation, VertxProperties properties) {
		this.annotation = annotation;
		this.mainVerticle = new MainVerticle(annotation, properties);
	}

	@Override
	public void start() throws ServerException {
		this.annotation.getVertx().apply(vertx -> {
			CompletableFuture<Void> startFuture = new CompletableFuture<>();
			vertx.deployVerticle(mainVerticle, res -> {
				if (res.succeeded()) {
					startFuture.complete(null);
				} else {
					startFuture.completeExceptionally(res.cause());
				}
			});

			// 监听状态
			startFuture.whenComplete((s, v) -> {
				if (v != null) {
					Logger.error("start server error", v);
					throw new RuntimeException(v);
				}
			});

			// 应该会阻塞在这里
			try {
				startFuture.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void stop() throws ServerException {
		this.annotation.getVertx().destroy(vertx -> {
			CompletableFuture<Void> stopFuture = new CompletableFuture<>();
			vertx.undeploy(mainVerticle.deploymentID(), res -> {
				if (res.succeeded()) {
					stopFuture.complete(null);
				} else {
					stopFuture.completeExceptionally(res.cause());
				}
			});

			// 应该会阻塞在这里
			try {
				stopFuture.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * 显示启动的服务
	 */
	@Override
	public String getAddresses() {
		return mainVerticle.getAddresses();
	}
}