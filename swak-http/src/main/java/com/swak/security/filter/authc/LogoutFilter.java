package com.swak.security.filter.authc;

import com.swak.common.entity.Result;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.security.filter.AdviceFilter;
import com.swak.security.subject.Subject;
import com.swak.security.utils.SecurityUtils;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 退出登录
 * @author lifeng
 */
public class LogoutFilter extends AdviceFilter {

	private String redirectUrl;

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	@Override
	protected boolean preHandle(HttpServerRequest request, HttpServerResponse response) {
		Subject subject = SecurityUtils.getSubject(request);
		if (subject.getPrincipal() != null) {
			subject.logout(request, response);
		}
		response.json().status(HttpResponseStatus.OK).buffer(Result.success().toJson());
		return false;
	}
}