package com.tmt.shop.web;

import com.swak.entity.Result;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.web.annotation.GetMapping;
import com.swak.reactivex.web.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * 需要用户才能访问
 * @author lifeng
 */
@RestController("/admin/user")
public class UserController {

	/**
	 * 得到用户
	 * @param request
	 * @return
	 */
	@GetMapping("get")
	public Mono<Result> user(HttpServerRequest request) {
		return Mono.just(Result.success(request.getSubject().getPrincipal()));
	}
}
