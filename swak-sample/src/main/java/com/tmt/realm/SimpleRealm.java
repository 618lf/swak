package com.tmt.realm;

import java.util.concurrent.CompletableFuture;

import com.swak.flux.security.context.AuthorizationInfo;
import com.swak.flux.security.context.SimpleAuthorizationInfo;
import com.swak.flux.security.exception.AuthenticationException;
import com.swak.flux.security.realm.Realm;
import com.swak.flux.transport.Principal;
import com.swak.flux.transport.Subject;
import com.swak.flux.transport.server.HttpServerRequest;

/**
 * 一个最简单的用户域
 * @author lifeng
 */
public class SimpleRealm implements Realm {

	/**
	 * 模拟一个用户
	 * 如果密码等错误，需要抛出异常，或者返回错误
	 */	
	@Override
	public CompletableFuture<Principal> doAuthentication(HttpServerRequest request) throws AuthenticationException {
		Principal principal = new Principal(0, "lifeng");
		return CompletableFuture.completedFuture(principal);
	}

	/**
	 * 为用户分配权限
	 */
	@Override
	public CompletableFuture<AuthorizationInfo> doGetAuthorizationInfo(Principal principal) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addRole("admin");
		return CompletableFuture.completedFuture(info);
	}

	@Override
	public void onLoginSuccess(Subject subject, HttpServerRequest request) {
		System.out.println("用户登录成功");
	}

	@Override
	public void onLoginFailure(HttpServerRequest request) {
		System.out.println("用户登录失败");
	}

	@Override
	public void onLogout(Subject subject) {
		System.out.println("用户登出");
	}

	@Override
	public void onInvalidate(String sessionId, String reason) {
		System.out.println("身份失效");
	}
}