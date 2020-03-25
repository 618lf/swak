package com.tmt.api.web;

import java.util.concurrent.CompletableFuture;

import com.swak.annotation.GetMapping;
import com.swak.annotation.RestApi;
import com.swak.entity.Result;

/**
 * 可以匿名访问 的 API
 * 
 * @author lifeng
 */
@RestApi(path = "/api/anno", value = "annoApi")
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
