package com.swak.security.filter.authz;

import com.swak.common.entity.Result;
import com.swak.common.exception.ErrorCode;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.security.filter.AccessControllerFilter;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 权限验证器
 * 
 * @author lifeng
 */
public abstract class AuthorizationFilter extends AccessControllerFilter {

	/**
	 * 权限验证
	 */
	protected boolean onAccessDenied(HttpServerRequest request, HttpServerResponse response) {
		response.json().status(HttpResponseStatus.OK).buffer(Result.error(ErrorCode.ACCESS_DENIED).toJson());
		return false;
	}
}