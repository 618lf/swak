package com.swak.vertx.security;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import com.swak.security.Permission;
import com.swak.security.jwt.JWTObject;
import com.swak.security.jwt.JWTPayload;
import com.swak.utils.Maps;
import com.swak.utils.Sets;
import com.swak.utils.StringUtils;
import com.swak.vertx.transport.Principal;
import com.swak.vertx.transport.Subject;
import com.swak.vertx.transport.Token;

import io.vertx.ext.web.RoutingContext;

/**
 * 安全的主体
 * 
 * @author lifeng
 */
public class SecuritySubject extends JWTObject implements Subject {

	private static String ID_ATTR = "id";
	private static String NAME_ATTR = "name";
	private static String ROLE_ATTR = "roles";
	private static String PERMISSION_ATTR = "permissions";

	private Principal principal; // 当前用户的身份
	private Set<String> roles;
	private Set<String> permissions;

	/**
	 * 必须传入 payload
	 * 
	 * @param payload
	 */
	public SecuritySubject() {
		this.map = Maps.newHashMap();
	}

	/**
	 * 必须传入 payload
	 * 
	 * @param payload
	 */
	public SecuritySubject(JWTPayload payload) {
		if (payload != null) {
			this.map = payload.getData();
		} else {
			this.map = Maps.newHashMap();
		}
		Serializable id = payload.get(ID_ATTR);
		String name = payload.get(NAME_ATTR);
		if (id != null) {
			this.setPrincipal(new Principal(id, name));
		}
	}

	@Override
	public Principal getPrincipal() {
		return principal;
	}

	@Override
	public SecuritySubject setPrincipal(Principal principal) {
		this.principal = principal;
		return this;
	}

	@Override
	public CompletionStage<Token> login(RoutingContext context) {
		return SecurityUtils.getSecurityManager().login(this, context);
	}

	@Override
	public CompletionStage<Boolean> isPermitted(Permission permission) {
		return SecurityUtils.getSecurityManager().isPermitted(this, permission);
	}

	@Override
	public CompletionStage<Boolean> hasRole(Permission role) {
		return SecurityUtils.getSecurityManager().hasRole(this, role);
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
	@Deprecated
	public CompletionStage<boolean[]> hasRoles(String... permissions) {
		return SecurityUtils.getSecurityManager().hasRoles(this, permissions);
	}

	@Override
	public CompletionStage<Boolean> hasAllRoles(String... permissions) {
		return SecurityUtils.getSecurityManager().hasAllRoles(this, permissions);
	}

	@Override
	public SecuritySubject setRoles(Set<String> roles) {
		this.roles = roles;
		return this;
	}

	@Override
	public SecuritySubject setPermissions(Set<String> permissions) {
		this.permissions = permissions;
		return this;
	}

	@Override
	public Set<String> getRoles() {
		if (this.roles == null) {
			String roles = this.get(ROLE_ATTR);
			if (StringUtils.isNotBlank(roles)) {
				this.roles = Sets.newHashSet(roles.split(","));
			}
		}
		return this.roles;
	}

	@Override
	public Set<String> getPermissions() {
		if (this.permissions == null) {
			String permissions = this.get(PERMISSION_ATTR);
			if (StringUtils.isNotBlank(permissions)) {
				this.permissions = Sets.newHashSet(permissions.split(","));
			}
		}
		return this.permissions;
	}

	@Override
	public JWTPayload toPayload() {
		this.put(ID_ATTR, principal.getId());
		this.put(NAME_ATTR, principal.getName());
		if (roles != null) {
			this.put(ROLE_ATTR, StringUtils.join(roles, ","));
		}
		if (permissions != null) {
			this.put(PERMISSION_ATTR, StringUtils.join(permissions, ","));
		}
		return new JWTPayload(this.map);
	}

	@Override
	public void destory() {
		this.roles = null;
		this.permissions = null;
		this.principal = null;
	}
}