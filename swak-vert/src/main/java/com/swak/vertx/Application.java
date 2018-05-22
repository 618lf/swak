package com.swak.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

/**
 * 自定义需要启动的类
 * 
 * @author lifeng
 */
public class Application {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);
		router.route().handler(context -> {
			// 所有的请求都会调用这个处理器处理
			HttpServerResponse response = context.response();
			response.putHeader("content-type", "text/plain");
			context.next();
		});
		router.get("/favicon.ico").handler(context ->{
			context.response().end("Favicon from Vert.x-Web!");
		});
		router.get("/admin").handler(context ->{
			context.response().end("Admin from Vert.x-Web!");
		});
		server.requestHandler(router::accept).listen(8080);
	}
}