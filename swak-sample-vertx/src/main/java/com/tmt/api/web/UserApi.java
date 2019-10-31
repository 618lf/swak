package com.tmt.api.web;

import java.util.concurrent.CompletableFuture;

import com.swak.entity.Result;
import com.swak.entity.Results;
import com.swak.vertx.annotation.GetMapping;
import com.swak.vertx.annotation.RestController;
import com.tmt.api.facade.UserServiceFacadeAsync;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;

import io.vertx.ext.web.RoutingContext;

/**
 * 商品 api
 * 
 * @author lifeng
 */
@RestController(path = "/api/user")
public class UserApi {

	@MotanReferer
	private UserServiceFacadeAsync goodsServiceRpc;

	/**
	 * 通过 rpc 获取数据
	 *
	 * @param context
	 * @return
	 */
	@GetMapping("/rpc_get")
	public CompletableFuture<Result> rpc_get(RoutingContext context) {
		return goodsServiceRpc.getUserAsync().toFuture().thenApply(Results.identity());
	}
}
