package com.tmt.realm;

import java.util.concurrent.CompletableFuture;

import com.swak.reactivex.transport.http.Principal;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.security.context.AuthorizationInfo;
import com.swak.security.context.SimpleAuthorizationInfo;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.realm.Realm;

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
		return null;
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