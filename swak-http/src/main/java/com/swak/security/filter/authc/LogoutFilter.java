package com.swak.security.filter.authc;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.http.Result;
import com.swak.security.filter.AdviceFilter;
import com.swak.security.subjct.Subject;
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
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Subject subject = SecurityUtils.getSubject();
		if (subject.getPrincipal() != null) {
			subject.logout(request, response);
		}
		response.json().status(HttpResponseStatus.OK).out(Result.success().toJson());
		return false;
	}
}