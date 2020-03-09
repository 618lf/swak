package com.swak.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * 测试各种类型的 Verticle
 * 
 * @author lifeng
 */
public class MainVertx extends AbstractVerticle {

	@Override
	public void start() throws Exception {

		/**
		 * 发布订单服务
		 */
		this.vertx.deployVerticle(new OrderVerticle());
	}

	/**
	 * 系统启动
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		/**
		 * 关闭线程监控
		 */
		System.setProperty("vertx.disableContextTimings", "true");

		/**
		 * 启用本地高性能传输协议
		 */
		VertxOptions options = new VertxOptions().setPreferNativeTransport(true);

		/**
		 * 创建默认的 vertx
		 */
		Vertx vertx = Vertx.vertx(options);

		/**
		 * 发布一个默认的 Verticle
		 */
		vertx.deployVerticle(new MainVertx(), res -> {
			System.out.println("发布成功！");

			vertx.eventBus().request("order", "123", new SendHandler());
		});
	}
}
