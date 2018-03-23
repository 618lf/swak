package com.swak.security.filter.authc;

import org.springframework.util.StringUtils;

import com.swak.http.ErrorCode;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.http.Result;
import com.swak.security.filter.AccessControllerFilter;
import com.swak.security.subjct.Subject;
import com.swak.security.utils.SecurityUtils;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 用户访问权限, 有可能需要访问用户状态
 * @author lifeng
 */
public class UserFilter extends AccessControllerFilter {

	@Override
	protected boolean isAccessAllowed(HttpServletRequest request,
			HttpServletResponse response, Object mappedValue) throws Exception {
		Subject subject = SecurityUtils.getSubject();
		return subject.getPrincipal() != null;
	}

	@Override
	protected boolean onAccessDenied(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Subject subject = SecurityUtils.getSubject();
		ErrorCode code = ErrorCode.NO_USER;
		if (StringUtils.hasText(subject.getReason())) {
			code = code.clone();
			code.setReason(subject.getReason());
		}
		response.json().status(HttpResponseStatus.OK).out(Result.error(code).toJson());
		return false;
	}
}