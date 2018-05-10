package com.tmt.shop.routers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.kotlin.Routers;
import com.swak.reactivex.web.function.RouterFunction;

import reactor.core.publisher.Mono;

/**
 * 可以使用基于kotlin 的配置来友好的配置路由
 * @author lifeng
 */
@Configuration
public class ShopRoutersConfiguration {

	/**
	 * 所有的代码都可以在这里配置
	 * @return
	 */
	@Bean
	public RouterFunction def() {
		return Routers.router((dsl) -> {
			dsl.GET("/favicon.ico", request -> {
				request.getResponse().cache(100);
				return Mono.just("hello lifeng1");
			});
			dsl.GET("/", request -> {
				return Mono.just("hello lifeng1");
			});
			dsl.GET("/admin/", request -> {
				return Mono.just("hello lifeng2");
			});
			dsl.GET("/admin/hello/say/hehe/{lala}", request -> {
				System.out.println(request.getPathVariables());
				return Mono.just("hello lifeng2");
			});
		});
	}
}