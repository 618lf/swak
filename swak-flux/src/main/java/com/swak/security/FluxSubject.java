package com.swak.security;

import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.reactivex.transport.http.Principal;
import com.swak.reactivex.transport.http.Session;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.exception.AuthenticationException;
import com.swak.utils.Sets;

import reactor.core.publisher.Mono;

public class FluxSubject implements Subject {

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
	public CompletionStage<Boolean> isPermitted(String permission) {
		return SecurityUtils.getSecurityManager().isPermitted(this, permission);
	}

	@Override
	public CompletionStage<boolean[]> isPermitted(String... permissions) {
		return SecurityUtils.getSecurityManager().isPermitted(this, permissions);
	}

	@Override
	public CompletionStage<Boolean> isPermittedAll(String... permissions) {
		return SecurityUtils.getSecurityManager().isPermittedAll(this, permissions);
	}

	@Override
	public CompletionStage<Boolean> hasRole(String role) {
		return SecurityUtils.getSecurityManager().hasRole(this, role);
	}

	@Override
	public CompletionStage<boolean[]> hasRoles(String... permissions) {
		return SecurityUtils.getSecurityManager().hasRoles(this, permissions);
	}

	@Override
	public CompletionStage<Boolean> hasAllRoles(String... permissions) {
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
	public Mono<Boolean> login(HttpServerRequest request, HttpServerResponse response) throws AuthenticationException {
		return SecurityUtils.getSecurityManager().login(this, request, response);
	}

	@Override
	public Mono<Boolean> login(Principal principal, HttpServerRequest request, HttpServerResponse response)
			throws AuthenticationException {
		return SecurityUtils.getSecurityManager().login(this, principal, request, response);
	}

	@Override
	public Mono<Boolean> logout(HttpServerRequest request, HttpServerResponse response) {
		return SecurityUtils.getSecurityManager().logout(this, request, response).doOnSuccess((t) -> {
			this.authenticated = false;
			this.roles = null;
			this.permissions = null;
		});
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
	public CompletionStage<Boolean> runAs(Principal principal) {
		return CompletableFuture.supplyAsync(() ->{
			if (this.getPrincipal() == null || principal == null || this.getPrincipal().equals(principal)) {
				return false;
			}

			// 创建 runAsPrincipals
			if (runAsPrincipals == null) {
				runAsPrincipals = new Stack<Principal>();
			}

			// 添加到顶部
			runAsPrincipals.push(principal);
			this.getSession().setRunAsPrincipals(runAsPrincipals);
			return true;
		});
	}

	/**
	 * 如果 runAsPrincipals 有身份信息则是以其他的身份在运行
	 */
	@Override
	public boolean isRunAs() {
		return runAsPrincipals != null && !runAsPrincipals.isEmpty();
	}

	/**
	 * 不是耗时间的操作可以使用 内置的 common-pool
	 */
	@Override
	public CompletionStage<Principal> releaseRunAs() {
		return CompletableFuture.supplyAsync(() ->{
			if (this.isRunAs()) {
				Principal principal = runAsPrincipals.pop();
				if (runAsPrincipals.isEmpty()) {
					runAsPrincipals = null;
				}
				this.getSession().setRunAsPrincipals(runAsPrincipals);
				return principal;
			}
			return this.getPrimaryPrincipal();
		});
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
