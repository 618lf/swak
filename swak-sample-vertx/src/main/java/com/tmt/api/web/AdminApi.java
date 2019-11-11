package com.tmt.api.web;

import java.util.concurrent.CompletableFuture;

import com.swak.annotation.GetMapping;
import com.swak.annotation.RequiresPermissions;
import com.swak.annotation.RequiresRoles;
import com.swak.annotation.RestController;
import com.swak.entity.Result;
import com.swak.vertx.transport.Subject;

/**
 * 需要Admin访问权限的 API
 * 
 * @author lifeng
 */
@RestController(path = "/api/admin", value = "adminApi")
public class AdminApi {

	/**
	 * 获取用户
	 * 
	 * @param subject
	 * @return
	 */
	@GetMapping("/get")
	public CompletableFuture<Result> get(Subject subject) {
		return CompletableFuture.completedFuture(Result.success());
	}

	/**
	 * 获取用户
	 * 
	 * @param subject
	 * @return
	 */
	@RequiresRoles({ "admin:role1", "admin:role2" })
	@GetMapping("/get_role")
	public CompletableFuture<Result> get_role(Subject subject) {
		return CompletableFuture.completedFuture(Result.success());
	}

	/**
	 * 获取用户
	 * 
	 * @param subject
	 * @return
	 */
	@RequiresRoles({ "admin:role1", "admin:role2" })
	@RequiresPermissions({ "admin:role1:op1", "admin:role2:op2" })
	@GetMapping("/get_permission")
	public CompletableFuture<Result> get_permission(Subject subject) {
		return CompletableFuture.completedFuture(Result.success());
	}
}
