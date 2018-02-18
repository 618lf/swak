package com.tmt.security;

import com.swak.http.HttpServletRequest;
import com.swak.security.context.AuthorizationInfo;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.principal.Principal;
import com.swak.security.realm.Realm;
import com.swak.security.subjct.Subject;

public class AuthenticationRealm implements Realm {

	/**
	 * 获得登录身份
	 */
	@Override
	public Principal doAuthentication(HttpServletRequest request) throws AuthenticationException {
		return null;
	}

	
	@Override
	public AuthorizationInfo getCachedAuthorizationInfo(Principal principal) {
		return null;
	}

	@Override
	public AuthorizationInfo doGetAuthorizationInfo(Principal principal) {
		return null;
	}

	@Override
	public void clearCachedAuthorizationInfo(Principal principal) {
		
	}

	@Override
	public void clearAllCachedAuthorizationInfo() {
		
	}

	@Override
	public void onLoginSuccess(Subject subject, HttpServletRequest request) {
		
	}

	@Override
	public void onLoginFailure(HttpServletRequest request) {
		
	}

	@Override
	public void onLogout(Subject subject) {
		
	}

	@Override
	public void onInvalidate(String sessionId, String reason) {
		
	}

	@Override
	public String resolveReason(String sessionId) {
		return null;
	}
}