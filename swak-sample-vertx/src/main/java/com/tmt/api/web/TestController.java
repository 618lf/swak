package com.tmt.api.web;

import java.util.concurrent.CompletableFuture;

import com.swak.validator.errors.BindErrors;
import com.swak.vertx.annotation.GetMapping;
import com.swak.vertx.annotation.PostMapping;
import com.swak.vertx.annotation.RestController;
import com.tmt.api.dto.GoodsDTO;

import io.vertx.ext.web.RoutingContext;

@RestController(path = "/api/test")
public class TestController {

	/**
	 * ab -n500000 -c100 http://192.168.0.14:8080/api/test/get
	 * 
	 * @param context
	 * @return
	 */
	@GetMapping("/get")
	public CompletableFuture<String> get(RoutingContext context) {
		return CompletableFuture.completedFuture("");
	}

	/**
	 * 验证参数
	 * 
	 * @param context
	 * @return
	 */
	@PostMapping("/validate")
	public CompletableFuture<String> validate(GoodsDTO goods, BindErrors errors) {
		System.out.println(errors);
		return CompletableFuture.completedFuture("");
	}
}
