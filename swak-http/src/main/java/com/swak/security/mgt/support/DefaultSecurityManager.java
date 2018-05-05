package com.swak.security.mgt.support;

import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.swak.common.exception.ErrorCode;
import com.swak.common.utils.StringUtils;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.security.context.AuthorizationInfo;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.mgt.SecurityManager;
import com.swak.security.principal.Principal;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.realm.Realm;
import com.swak.security.subject.DefaultSubject;
import com.swak.security.subject.Subject;

/**
 * 默认的安全管理器
 * @author lifeng
 */
public class DefaultSecurityManager implements SecurityManager {

	private final Realm realm;
	private final PrincipalStrategy principalStrategy;
	
	public DefaultSecurityManager(Realm realm, PrincipalStrategy principalStrategy) {
		this.realm = realm;
		this.principalStrategy = principalStrategy;
	}

	@Override
	public boolean isPermitted(Subject subject, String permission) {
		return this.loadPermissions(subject).contains(permission);
	}

	@Override
	public boolean[] isPermitted(Subject subject, String... permissions) {
		Set<String> _permissions = this.loadPermissions(subject);
		boolean[] result;
        if (permissions != null && permissions.length != 0) {
            int size = permissions.length;
            result = new boolean[size];
            int i = 0;
            for (String p : permissions) {
                result[i++] = !CollectionUtils.isEmpty(_permissions) && _permissions.contains(p);
            }
        } else {
            result = new boolean[0];
        }
        return result;
	}

	@Override
	public boolean isPermittedAll(Subject subject, String... permissions) {
		Set<String> _permissions = this.loadPermissions(subject);
		if (CollectionUtils.isEmpty(_permissions)) {
			return false;
		}
		if (permissions != null && permissions.length != 0) {
            for (String p : permissions) {
                if (!_permissions.contains(p)) {
                    return false;
                }
            }
        }
        return true;
	}

	@Override
	public boolean hasRole(Subject subject, String role) {
		return this.loadRoles(subject).contains(role);
	}

	@Override
	public boolean[] hasRoles(Subject subject, String... roles) {
		Set<String> _roles = this.loadRoles(subject);
		boolean[] result;
        if (roles != null && roles.length != 0) {
            int size = roles.length;
            result = new boolean[size];
            int i = 0;
            for (String p : roles) {
                result[i++] = !CollectionUtils.isEmpty(_roles) && _roles.contains(p);
            }
        } else {
            result = new boolean[0];
        }
        return result;
	}

	@Override
	public boolean hasAllRoles(Subject subject, String... roles) {
		Set<String> _roles = this.loadRoles(subject);
		if (CollectionUtils.isEmpty(_roles)) {
			return false;
		}
		if (roles != null && roles.length != 0) {
            for (String p : roles) {
                if (!_roles.contains(p)) {
                    return false;
                }
            }
        }
        return true;
	}
	
	/**
	 * 加载权限
	 * @param subject
	 * @return
	 */
	private Set<String> loadPermissions(Subject subject) {
		Set<String> permissions = subject.getPermissions();
		if (permissions == null) {
			AuthorizationInfo authorization = realm.getCachedAuthorizationInfo(subject.getPrincipal());
			subject.setPermissions(authorization.getPermissions());
			subject.setRoles(authorization.getRoles());
		}
		return subject.getPermissions();
	}
	
	/**
	 * 加载角色
	 * @param subject
	 * @return
	 */
	private Set<String> loadRoles(Subject subject) {
		Set<String> roles = subject.getRoles();
		if (roles == null) {
			AuthorizationInfo authorization = realm.getCachedAuthorizationInfo(subject.getPrincipal());
			subject.setPermissions(authorization.getPermissions());
			subject.setRoles(authorization.getRoles());
		}
		return subject.getRoles();
	}

	@Override
	public void login(Subject subject, HttpServerRequest request, HttpServerResponse response) throws AuthenticationException {
		
		// 验证
		Principal principal;
		try {
			principal = realm.doAuthentication(request);
		} catch (AuthenticationException ae) {
			this.onLoginFailure(request, response);
			throw ae;
		}
		
		// 必须返回身份
		if (principal == null) {
			this.onLoginFailure(request, response);
			throw new AuthenticationException(ErrorCode.U_P_FAILURE);
		}
		
		// 登录成功 保存用户凭证，设置到subject中即可
		this.login(subject, principal, request, response);
	}
	
	/**
	 * 指定了身份来登录
	 */
	@Override
	public void login(Subject subject, Principal principal, HttpServerRequest request, HttpServerResponse response) {
		this.login(subject, principal, true, request, response);
	}
	
	/**
	 * 真实的登录
	 * @param subject
	 * @param principal
	 * @param request
	 * @param response
	 */
	private void login(Subject subject, Principal principal, boolean authenticated, HttpServerRequest request, HttpServerResponse response) {
		// 设置相关信息到主体中
		subject.setPrincipal(principal);  subject.setAuthenticated(authenticated);
		
		// 创建身份
		principalStrategy.createPrincipal(subject, request, response);
		
		// 通知登录成功
		this.onLoginSuccess(subject, request, response);
	}

	/**
	 * 退出登录
	 */
	@Override
	public void logout(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		principalStrategy.invalidatePrincipal(subject, request, response);
		this.onLogout(subject, request, response);
	}

	/**
	 * 获取用户信息
	 */
	@Override
	public Subject createSubject(HttpServerRequest request, HttpServerResponse response) {
		
		// 基本的 Subject
		Subject subject = new DefaultSubject();
		
		// 获取身份
		principalStrategy.resolvePrincipal(subject, request, response);
		
		// session 失效的原因
		if (StringUtils.hasText(subject.getSessionId()) 
				&& subject.getSession() == null) {
			subject.setReason(this.realm.resolveReason(subject.getSessionId()));
		}
		
		return subject;
	}
	
	/**
	 * 将此身份失效
	 */
	@Override
	public void invalidate(String sessionId, String reason) {
		principalStrategy.invalidatePrincipal(sessionId);
		this.realm.onInvalidate(sessionId, reason);
	}
	
	/**
	 * 登录成功
	 * @param subject
	 */
	void onLoginSuccess(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		realm.onLoginSuccess(subject, request);
	}
	
	/**
	 * 登录失败
	 * @param request
	 */
	void onLoginFailure(HttpServerRequest request, HttpServerResponse response) {
		realm.onLoginFailure(request);
	}
	
	/**
	 *  退出登录
	 */
	void onLogout(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		realm.onLogout(subject);
	}
}