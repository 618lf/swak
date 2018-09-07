package com.tmt.api.web;

import java.util.concurrent.CompletableFuture;

import com.swak.vertx.annotation.GetMapping;
import com.swak.vertx.annotation.RestController;
import com.swak.vertx.annotation.ServiceReferer;
import com.tmt.api.entity.Goods;
import com.tmt.api.facade.GoodsServiceFacadeAsync;

import io.vertx.ext.web.RoutingContext;

/**
 * 商品 api
 * 
 * @author lifeng
 */
@RestController("/api/goods")
public class GoodsController {

	@ServiceReferer
	private GoodsServiceFacadeAsync goodsService;

	/**
	 * get参数或 post 参数
	 * get 参数： ?name=xxx
	 * post参数： xxx=yyy
	 * @param context
	 * @return
	 */
	@GetMapping("/get")
	public CompletableFuture<String> get(RoutingContext context) {
		return goodsService.sayHello();
	}
	
	/**
	 * rest 参数: /get/123
	 * @param id
	 * @return
	 */
	@GetMapping("/get/:id")
	public CompletableFuture<String> get(String id) {
		System.out.println(id);
		return goodsService.sayHello();
	}
	
	/**
	 * rest 参数: /get/123
	 * @param id
	 * @return
	 */
	@GetMapping("/get/:id/:name")
	public CompletableFuture<String> get(Goods goods) {
		System.out.println("内部序列:" + goods.getId());
		System.out.println("内部序列:" + goods.getName());
		return goodsService.sayHello();
	}
}