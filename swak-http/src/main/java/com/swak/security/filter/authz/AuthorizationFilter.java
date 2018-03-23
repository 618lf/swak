package com.swak.security.filter.authz;

import java.io.IOException;

import com.swak.http.ErrorCode;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.http.Result;
import com.swak.security.filter.AccessControllerFilter;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 权限验证器
 * @author lifeng
 */
public abstract class AuthorizationFilter extends AccessControllerFilter{

	/**
	 * 权限验证
	 */
	protected boolean onAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.json().status(HttpResponseStatus.OK).out(Result.error(ErrorCode.ACCESS_DENIED).toJson());
		return false;
	}
}