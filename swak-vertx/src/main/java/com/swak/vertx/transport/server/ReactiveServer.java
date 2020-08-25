package com.swak.vertx.transport.server;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.context.EndPoints;
import com.swak.reactivex.context.Server;
import com.swak.reactivex.context.ServerException;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.protocol.http.RouterHandler;
import com.swak.vertx.protocol.ws.WebSocketHandler;
import com.swak.vertx.transport.VertxProxy;
import com.swak.vertx.transport.vertx.MainVerticle;

import io.vertx.core.DeploymentOptions;

/**
 * 发布服务的一个入口
 *
 * @author: lifeng
 * @date: 2020/3/29 21:14
 */
public class ReactiveServer implements Server {

	private static Logger Logger = LoggerFactory.getLogger(ReactiveServer.class);
	private final MainVerticle mainVerticle;
	private final VertxProxy vertx;

	public ReactiveServer(VertxProxy vertx, VertxProperties properties, RouterHandler routerHandler,
			WebSocketHandler webSocketHandler) {
		this.vertx = vertx;
		this.mainVerticle = new MainVerticle(routerHandler, webSocketHandler, properties);
	}

	@Override
	public void start() throws ServerException {

		// 发布服务类
		this.vertx.apply(vertx -> {
			CompletableFuture<Void> startFuture = new CompletableFuture<>();

			// 以worker 的方式发布
			DeploymentOptions options = new DeploymentOptions().setWorker(true);

			// 发布启动 主服务
			vertx.deployVerticle(mainVerticle, options, res -> {
				if (res.succeeded()) {
					startFuture.complete(null);
				} else {
					startFuture.completeExceptionally(res.cause());
				}
			});

			// 监听状态
			startFuture.whenComplete((s, v) -> {
				if (v != null) {
					Logger.error("Start Server Error:", v);
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
		this.vertx.destroy(vertx -> {
			CompletableFuture<Void> stopFuture = new CompletableFuture<>();
			vertx.close(res -> {
				if (res.succeeded()) {
					stopFuture.complete(null);
				} else {
					stopFuture.completeExceptionally(res.cause());
				}
			});
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
	public EndPoints getEndPoints() {
		return this.mainVerticle.getEndPoints();
	}
}