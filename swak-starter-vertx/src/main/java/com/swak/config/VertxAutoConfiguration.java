package com.swak.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.vertx.properties.VertxProperties;
import com.swak.vertx.utils.Lifecycle;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;

/**
 * vertx 服务器配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(VertxProperties.class)
@EnableConfigurationProperties(VertxProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class VertxAutoConfiguration {

	/**
	 * 创建 唯一的 Vertx 
	 * @return
	 */
    @Bean
    public Vertx vertx() {
        Vertx vertx = Vertx.vertx();
        Lifecycle.vertx = vertx;
        return vertx;
    }
    
    /**
     * 创建 路由对象
     * @return
     */
    @Bean
    public Router router() {
        Vertx vertx = Lifecycle.vertx;
        Router router = Router.router(vertx);
        router.route().handler(CookieHandler.create());
        Lifecycle.router = router;
        return router;
    }
    
    /**
     * 启动一个 http 服务器
     * @return
     */
    @Bean
    public HttpServer httpServer() {
        return Lifecycle.vertx.createHttpServer();
    }
}