package com.tmt.shop.web;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.Principal;
import com.swak.reactivex.web.annotation.GetMapping;
import com.swak.reactivex.web.annotation.PathVariable;
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
	
	/**
	 * 需要管理员
	 * @param request
	 * @return
	 */
	@GetMapping("/check/{role}")
	public Mono<Result> check(HttpServerRequest request, @PathVariable String role) {
		return request.getSubject().hasRole(role).map(r -> {
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
	public Mono<Result> runas(HttpServerRequest request) {
		Principal principal = new Principal(); principal.setId(0L); principal.setAccount("超级管理员");
		return request.getSubject().runAs(principal).map(b ->{
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
	public Mono<Result> release(HttpServerRequest request) {
		return request.getSubject().releaseRunAs().map(b ->{
			return Result.success(ErrorCode.OPERATE_SECCESS.toJson());
		});
	}
}
