package com.swak.security.filter.authc;

import org.springframework.util.StringUtils;

import com.swak.exception.ErrorCode;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.Subject;
import com.swak.reactivex.web.Result;
import com.swak.security.filter.AccessControllerFilter;
import com.swak.security.utils.SecurityUtils;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 用户访问权限, 有可能需要访问用户状态
 * @author lifeng
 */
public class UserFilter extends AccessControllerFilter {

	@Override
	protected boolean isAccessAllowed(HttpServerRequest request,
			HttpServerResponse response, Object mappedValue) {
		Subject subject = SecurityUtils.getSubject(request);
		return subject.getPrincipal() != null;
	}

	@Override
	protected boolean onAccessDenied(HttpServerRequest request, HttpServerResponse response) {
		Subject subject = SecurityUtils.getSubject(request);
		ErrorCode code = ErrorCode.NO_USER;
		if (StringUtils.hasText(subject.getReason())) {
			try {
				code = code.clone();
			} catch (CloneNotSupportedException e) {}
			code.setReason(subject.getReason());
		}
		response.json().status(HttpResponseStatus.OK).buffer(Result.error(code).toJson());
		return false;
	}
}