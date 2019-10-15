package com.swak.vertx.security.realm;

import java.util.concurrent.CompletionStage;

import com.swak.vertx.transport.AuthorizationInfo;
import com.swak.vertx.transport.Principal;

/**
 * 现在只有权限校验这一段
 * 
 * @author lifeng
 */
public interface Realm {

	/**
	 * 获取当前身份的权限信息
	 * @param principal
	 * @return
	 */
	CompletionStage<AuthorizationInfo> doGetAuthorizationInfo(Principal principal);
}