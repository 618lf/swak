package com.swak.security.subjct.support;

import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;

import com.swak.common.utils.Sets;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.security.context.ThreadContext;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.principal.Principal;
import com.swak.security.principal.Session;
import com.swak.security.subjct.Subject;
import com.swak.security.utils.SecurityUtils;

/**
 * 默认的主体
 * @author lifeng
 */
public class DefaultSubject implements Subject {

	private String sessionId; // 登录之后分配的会话ID
	private Principal principal; // 当前用户的身份
	private Stack<Principal> runAsPrincipals; // 使用的运行的身份
	private boolean authenticated;
	private Set<String> roles;
	private Set<String> permissions;
	private Session session;
	private String reason;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	@Override
	public Session getSession() {
		return session;
	}
	@Override
	public void setSession(Session session) {
		this.session = session;
		if (this.session != null) {
			this.sessionId = this.session.getId();
			this.principal = this.session.getPrincipal();
			this.authenticated = this.session.isAuthenticated();
			this.runAsPrincipals = this.session.getRunAsPrincipals();
		}
	}

	@Override
	public Principal getPrincipal() {
		return getPrimaryPrincipal();
	}
	
	@Override
	public Set<Principal> getPrincipals() {
		Set<Principal> principals = Sets.newHashSet();
		if (runAsPrincipals != null && runAsPrincipals.size() != 0) {
			principals.addAll(runAsPrincipals);
		}
		principals.add(principal);
		return principals;
	}

	@Override
	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	// 得到主身份
	private Principal getPrimaryPrincipal() {
		if (runAsPrincipals == null || runAsPrincipals.isEmpty()) {
			return principal;
		}
		return runAsPrincipals.peek();
	}

	public Stack<Principal> getRunAsPrincipals() {
		return runAsPrincipals;
	}

	public void setRunAsPrincipals(Stack<Principal> runAsPrincipals) {
		this.runAsPrincipals = runAsPrincipals;
	}

	@Override
	public boolean isPermitted(String permission) {
		return SecurityUtils.getSecurityManager().isPermitted(this, permission);
	}

	@Override
	public boolean[] isPermitted(String... permissions) {
		return SecurityUtils.getSecurityManager().isPermitted(this, permissions);
	}

	@Override
	public boolean isPermittedAll(String... permissions) {
		return SecurityUtils.getSecurityManager().isPermittedAll(this, permissions);
	}

	@Override
	public boolean hasRole(String role) {
		return SecurityUtils.getSecurityManager().hasRole(this, role);
	}

	@Override
	public boolean[] hasRoles(String... permissions) {
		return SecurityUtils.getSecurityManager().hasRoles(this, permissions);
	}

	@Override
	public boolean hasAllRoles(String... permissions) {
		return SecurityUtils.getSecurityManager().hasAllRoles(this, permissions);
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public boolean isRemembered() {
		return this.getPrincipal() != null && !isAuthenticated();
	}

	@Override
	public void login(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		SecurityUtils.getSecurityManager().login(this, request, response);
	}
	
	@Override
	public void login(Principal principal, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		SecurityUtils.getSecurityManager().login(this, principal, request, response);
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		try {
			SecurityUtils.getSecurityManager().logout(this, request, response);
		} finally {
			this.authenticated = false;
			this.roles = null;
			this.permissions = null;
		}
	}

	@Override
	public <V> V execute(Callable<V> callable){
		try {
			ThreadContext.remove();
	        ThreadContext.bind(this);
            return callable.call();
        } catch (Exception t) {
            throw new RuntimeException(t);
        } finally {
        	ThreadContext.remove();
        }
	}

	@Override
	public Set<String> getRoles() {
		return roles;
	}

	@Override
	public Set<String> getPermissions() {
		return permissions;
	}
	
	@Override
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	@Override
	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

	@Override
	public void runAs(Principal principal) {
		if (this.getPrincipal() == null || principal == null) {
			String msg = "This subject does not yet have an identity.";
            throw new IllegalStateException(msg);
		}
		
		// 创建 runAsPrincipals
		if (runAsPrincipals == null) {
			runAsPrincipals = new Stack<Principal>();
		}
		
		// 添加到顶部
		runAsPrincipals.push(principal);
		this.getSession().setRunAsPrincipals(runAsPrincipals);
	}
	
	/**
	 * 如果 runAsPrincipals 有身份信息则是以其他的身份在运行
	 */
	@Override
	public boolean isRunAs() {
		return runAsPrincipals != null && !runAsPrincipals.isEmpty();
	}

	@Override
	public Object releaseRunAs() {
		if (this.isRunAs()) {
			Object principal = runAsPrincipals.pop();
			if (runAsPrincipals.isEmpty()) {
				runAsPrincipals = null;
			}
			this.getSession().setRunAsPrincipals(runAsPrincipals);
		    return principal;
		}
		return null;
	}

	@Override
	public void destory() {
		this.authenticated = false;
		this.roles = null;
		this.principal = null;
		this.permissions = null;
		if (this.session != null) {
			this.session.destory();
		}
		this.session = null;
	}
}