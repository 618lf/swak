package com.tmt.api.web;

import com.swak.vertx.annotation.GetMapping;
import com.swak.vertx.annotation.RestController;
import com.swak.vertx.annotation.ServiceReferer;
import com.tmt.api.facade.GoodsServiceFacade;

import io.vertx.ext.web.RoutingContext;

/**
 * 商品 api
 * @author lifeng
 */
@RestController("/api/goods")
public class GoodsController {
	
	@ServiceReferer
	private GoodsServiceFacade goodsService;

	@GetMapping("/get")
	public void get(RoutingContext context) {
		goodsService.sayHello();
		context.response().end("111");
	}
}