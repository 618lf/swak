package com.swak.vertx.security.realm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.vertx.transport.AuthorizationInfo;
import com.swak.vertx.transport.Principal;
import com.swak.vertx.transport.Subject;

import io.vertx.ext.web.RoutingContext;

/**
 * 获取权限域信息
 *
 * @author: lifeng
 * @date: 2020/3/29 20:45
 */
public interface Realm {

	/**
	 * 直接验证，通过就返回用户信息, 不通过就返回 null <br>
	 * 可以直接将权限在前端设置好，通常小程序、微信端注册和登陆是一体的，所有不需要在后端设置 <br>
	 * 
	 * @param subject 主体
	 * @param context 请求上下文
	 * @return 用户身份信息
	 */
	default CompletionStage<Principal> doAuthentication(Subject subject, RoutingContext context) {
		return CompletableFuture.completedFuture(subject.getPrincipal());
	}

	/**
	 * 获取当前身份的权限信息
	 *
	 * @param subject 身份信息
	 * @return 权限信息
	 */
	CompletionStage<AuthorizationInfo> doGetAuthorizationInfo(Subject subject);
}