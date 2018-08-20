package com.swak.vertx.transport;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.context.Server;
import com.swak.reactivex.context.ServerException;
import com.swak.vertx.config.AnnotationBean;

import io.vertx.core.Vertx;

/**
 * 发布服务的一个入口
 * 
 * @author lifeng
 */
public class ReactiveServer implements Server {

	private static Logger Logger = LoggerFactory.getLogger(ReactiveServer.class);
	private final MainVerticle mainVerticle;
	private final Vertx vertx;

	public ReactiveServer(AnnotationBean annotationBean, MainVerticle mainVerticle) {
		this.vertx = annotationBean.getVertx();
		this.mainVerticle = mainVerticle;
	}

	@Override
	public void start() throws ServerException {
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
	}

	@Override
	public void stop() throws ServerException {
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
	}

	/**
	 * 显示启动的服务
	 */
	@Override
	public String getAddresses() {
		return mainVerticle.getAddresses();
	}
}