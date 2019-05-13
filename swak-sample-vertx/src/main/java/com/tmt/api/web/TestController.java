package com.tmt.api.web;

import java.util.concurrent.CompletableFuture;

import com.swak.vertx.annotation.GetMapping;
import com.swak.vertx.annotation.RestController;

import io.vertx.ext.web.RoutingContext;

@RestController(path = "/api/test")
public class TestController {

	/**
	 * ab -n500000 -c100  http://192.168.0.16:8080/api/test/get
	 * 
	 * @param context
	 * @return
	 */
	@GetMapping("/get")
	public CompletableFuture<String> get(RoutingContext context) {
		return CompletableFuture.completedFuture("");
	}
}
