package com.tmt.shop.web;

import java.util.concurrent.CompletionStage;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.flux.transport.Principal;
import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.web.annotation.GetMapping;
import com.swak.flux.web.annotation.PathVariable;
import com.swak.flux.web.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * 需要用户才能访问
 * @author lifeng
 */
@RestController(path = "/admin/user")
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
	
	/**
	 * 需要管理员
	 * @param request
	 * @return
	 */
	@GetMapping("/check/{role}")
	public CompletionStage<Result> check(HttpServerRequest request, @PathVariable String role) {
		return request.getSubject().hasRole(role).thenApply(r -> {
			if (r) {
				return Result.success(request.getSubject().getPrincipal());
			}
			return Result.error(ErrorCode.ACCESS_DENIED.toJson());
		});
	}
	
	/**
	 * 需要管理员
	 * @param request
	 * @return
	 */
	@GetMapping("admin")
	public Mono<Result> admin(HttpServerRequest request) {
		return Mono.just(Result.success(request.getSubject().getPrincipal()));
	}
	
	/**
	 * 需要系统管理员
	 * @param request
	 * @return
	 */
	@GetMapping("system")
	public Mono<Result> system(HttpServerRequest request) {
		return Mono.just(Result.success(request.getSubject().getPrincipal()));
	}
	
	/**
	 * 以管理员的身份运行
	 * @param request
	 * @return
	 */
	@GetMapping("/runas/root")
	public CompletionStage<Result> runas(HttpServerRequest request) {
		Principal principal = new Principal(); principal.setId(0L); principal.setAccount("超级管理员");
		return request.getSubject().runAs(principal).thenApply(b ->{
			if (b) {
				return Result.success(ErrorCode.OPERATE_SECCESS.toJson());
			}
			return Result.error(ErrorCode.OPERATE_FAILURE.toJson());
		});
	}
	
	/**
	 * 以管理员的身份运行
	 * @param request
	 * @return
	 */
	@GetMapping("/runas/release")
	public CompletionStage<Result> release(HttpServerRequest request) {
		return request.getSubject().releaseRunAs().thenApply(b ->{
			return Result.success(ErrorCode.OPERATE_SECCESS.toJson());
		});
	}
}
