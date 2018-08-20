package com.tmt.api.web;

import java.util.concurrent.CompletableFuture;

import com.swak.vertx.annotation.GetMapping;
import com.swak.vertx.annotation.RestController;
import com.swak.vertx.annotation.ServiceReferer;
import com.tmt.api.facade.GoodsServiceFacade;

import io.vertx.ext.web.RoutingContext;

/**
 * 商品 api
 * 
 * @author lifeng
 */
@RestController("/api/goods")
public class GoodsController {

	@ServiceReferer
	private GoodsServiceFacade goodsService;

	@GetMapping("/get")
	public CompletableFuture<String> get(RoutingContext context) {
		System.out.println(Thread.currentThread());
		return goodsService.sayHello();
	}
}