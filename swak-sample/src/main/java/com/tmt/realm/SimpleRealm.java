package com.tmt.realm;

import com.swak.exception.ErrorCode;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.Principal;
import com.swak.reactivex.Subject;
import com.swak.reactivex.web.WebUtils;
import com.swak.security.context.AuthenticationToken;
import com.swak.security.context.AuthorizationInfo;
import com.swak.security.context.SimpleAuthorizationInfo;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.exception.CaptchatAuthenException;
import com.swak.security.exception.UsernamePasswordException;
import com.swak.security.realm.Realm;
import com.swak.utils.StringUtils;

import reactor.core.publisher.Mono;

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
	public Mono<Principal> doAuthentication(HttpServerRequest request) throws AuthenticationException {
		
		// 获取的相关的参数
		String username = WebUtils.getCleanParam(request, AuthenticationToken.username);
		String password = WebUtils.getCleanParam(request, AuthenticationToken.password);
		String captcha = WebUtils.getCleanParam(request, AuthenticationToken.captcha);
		
		// 是否有用户信息
		if (!(StringUtils.hasText(username) && StringUtils.hasText(password))) {
			return Mono.error(new UsernamePasswordException(ErrorCode.U_P_FAILURE));
		}
		
		// 用户是否需要验证验证码 -- 是否超过次数
		if (StringUtils.hasText(captcha)) {
			return Mono.error(new CaptchatAuthenException(ErrorCode.CAPTCHA_FAILURE));
		}
		
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