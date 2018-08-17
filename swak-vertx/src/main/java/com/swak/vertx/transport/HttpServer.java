package com.swak.vertx.transport;

import static com.swak.Application.APP_LOGGER;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.swak.vertx.properties.VertxProperties;
import com.swak.vertx.utils.Lifecycle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

/**
 * http服务器
 * 
 * @author lifeng
 */
public class HttpServer extends AbstractVerticle implements InitializingBean {

	@Autowired
	private VertxProperties vertxProperties;

	@Override
	public void start() throws Exception {
		super.start();
		
		// 路由
		Router router = Lifecycle.router;
		
		// 先简单测试下
		router.route("/some/path/").handler(context ->{
			HttpServerResponse response = context.response();
			response.putHeader("content-type", "text/plain");
			response.end("Hello World from Vert.x-Web!");
		});
		
		Lifecycle.vertx.createHttpServer().requestHandler(router::accept).listen(vertxProperties.getPort());
	}

	/**
	 * 通过 spring 自启动
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Lifecycle.vertx.deployVerticle(this, handler -> {
			APP_LOGGER.debug("start http service on port " + vertxProperties.getPort());
		});
	}
}