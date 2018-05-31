package com.tmt.realm;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.Principal;
import com.swak.reactivex.Subject;
import com.swak.security.context.AuthorizationInfo;
import com.swak.security.context.SimpleAuthorizationInfo;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.realm.Realm;

import reactor.core.publisher.Mono;

/**
 * 一个最简单的用户域
 * @author lifeng
 */
public class SimpleRealm implements Realm {

	/**
	 * 模拟一个用户
	 */	
	@Override
	public Mono<Principal> doAuthentication(HttpServerRequest request) throws AuthenticationException {
		return Mono.just(new Principal(1L, "lifeng"));
	}

	/**
	 * 为用户分配权限
	 */
	@Override
	public Mono<AuthorizationInfo> doGetAuthorizationInfo(Principal principal) {
		return Mono.fromSupplier(() ->{
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			info.addRole("admin");
			return info;
		});
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