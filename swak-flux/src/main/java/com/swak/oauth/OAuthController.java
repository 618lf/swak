package com.swak.oauth;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.Principal;

/**
 * 针对第三方登录，或其他快捷登录提供的api
 * @author lifeng
 */
public interface OAuthController {

	/**
	 * 通过 principal 登录系统
	 * @param request
	 * @param response
	 * @param principal
	 * @return
	 */
	default void login(HttpServerRequest request, Principal principal) {
		request.getSubject().login(principal, request, request.getResponse());
	}
}