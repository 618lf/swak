package com.tmt.api;

import com.swak.vertx.annotation.GetMapping;
import com.swak.vertx.annotation.RestController;

import io.vertx.ext.web.RoutingContext;

/**
 * 商品 api
 * @author lifeng
 */
@RestController("/api/goods")
public class GoodsController {

	@GetMapping("/get")
	public void get(RoutingContext context) {
		
		context.response().end("111");
	}
}