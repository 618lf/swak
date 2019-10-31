package com.tmt.api.web;

import java.util.concurrent.CompletableFuture;

import com.swak.entity.Result;
import com.swak.vertx.annotation.GetMapping;
import com.swak.vertx.annotation.RestController;

/**
 * 可以匿名访问 的 API
 * 
 * @author lifeng
 */
@RestController(path = "/api/anno", value = "annoApi")
public class AnnoApi {

	/**
	 * 获取用户
	 * 
	 * @param subject
	 * @return
	 */
	@GetMapping("/get")
	public CompletableFuture<Result> get() {
		return CompletableFuture.completedFuture(Result.success());
	}
}
