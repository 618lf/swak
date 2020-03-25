package com.tmt.api.web;

import java.util.concurrent.CompletableFuture;

import com.swak.Constants;
import com.swak.annotation.FluxReferer;
import com.swak.annotation.GetMapping;
import com.swak.annotation.RestController;
import com.swak.entity.Result;
import com.swak.rabbit.annotation.Publisher;
import com.tmt.api.entity.Goods;
import com.tmt.api.event.GoodsEvent;
import com.tmt.api.exception.GoodsException;
import com.tmt.api.facade.GoodsServiceFacadeAsync;
import com.tmt.api.facade.GoodsServiceFacadeAsyncx;
import com.tmt.publisher.GoodsEventPublisher;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;

import io.vertx.ext.web.RoutingContext;

/**
 * 商品 api
 * 
 * @author lifeng
 */
@RestController(path = "/api/goods")
public class GoodsController {

	@Publisher(queue = "swak.test.goods")
	private GoodsEventPublisher publisher;
	@Publisher(queue = "swak.test.goods")
	private GoodsEventPublisher publisher2;
	@FluxReferer
	private GoodsServiceFacadeAsyncx goodsService;
	@MotanReferer
	private GoodsServiceFacadeAsync goodsServiceRpc;

	/**
	 * 通过 rpc 获取数据
	 *
	 * @param context
	 * @return
	 */
	@GetMapping("/rpc_get")
	public CompletableFuture<Result> rpc_get(RoutingContext context) {
		return goodsServiceRpc.sayHelloAsync().toFuture().thenApply(o -> Result.success(o));
	}

	/**
	 * get参数或 post 参数 get 参数： ?name=xxx post参数： xxx=yyy
	 * 
	 * @param context
	 * @return
	 */
	@GetMapping("/get")
	public CompletableFuture<String> get(RoutingContext context) {
		return goodsService.sayHello().thenCompose(msg -> {
			return publisher.post(GoodsEvent.of()).thenApply(res -> "1");
		}).exceptionally(r -> {
			Throwable e = r.getCause() != null ? r.getCause() : r;
			if (e instanceof GoodsException) {
				return "商品錯誤！";
			}
			return "错误处理！";
		});
	}

	/**
	 * rest 参数: /get/123
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/get/:id")
	public CompletableFuture<String> get(String id) {
		System.out.println(id);
		return goodsService.sayHello().thenApply(msg -> {
			System.out.println("当前线程：" + Thread.currentThread().getName());
			return msg.getResult();
		});
	}

	/**
	 * rest 参数: /get/123
	 * 
	 * @param goods 商品
	 * @return
	 */
	@GetMapping("/get/:id/:name")
	public CompletableFuture<String> get(Goods goods) {
		System.out.println("内部序列:" + goods.getId());
		System.out.println("内部序列:" + goods.getName());
		return goodsService.sayHello().thenApply(msg -> {
			return msg.getResult();
		});
	}

	/**
	 * 重定向
	 * 
	 * @param context
	 * @return
	 */
	@GetMapping("/rt")
	public CompletableFuture<String> rt(RoutingContext context) {
		return CompletableFuture.completedFuture(Constants.REDIRECT_URL_PREFIX + "http://www.catax.cn/admin");
	}
}