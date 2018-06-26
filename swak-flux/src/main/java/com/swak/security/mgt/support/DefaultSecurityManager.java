package com.swak.security.mgt.support;

import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.swak.eventbus.system.SystemEventPublisher;
import com.swak.reactivex.transport.http.Principal;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.FluxSubject;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.mgt.SecurityManager;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.realm.Realm;

import reactor.core.publisher.Mono;

/**
 * 默认的安全管理器
 * @author lifeng
 */
public class DefaultSecurityManager implements SecurityManager {

	private final Realm realm;
	private final PrincipalStrategy principalStrategy;
	private final SystemEventPublisher eventPublisher;
	
	public DefaultSecurityManager(Realm realm, PrincipalStrategy principalStrategy,
			SystemEventPublisher eventPublisher) {
		this.realm = realm;
		this.principalStrategy = principalStrategy;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public Mono<Boolean> isPermitted(Subject subject, String permission) {
		return this.loadPermissions(subject).map(ps -> ps.contains(permission));
	}

	@Override
	public Mono<boolean[]> isPermitted(Subject subject, String... permissions) {
		return this.loadPermissions(subject).map(ps ->{
			boolean[] result;
	        if (permissions != null && permissions.length != 0) {
	            int size = permissions.length;
	            result = new boolean[size];
	            int i = 0;
	            for (String p : permissions) {
	                result[i++] = !CollectionUtils.isEmpty(ps) && ps.contains(p);
	            }
	        } else {
	            result = new boolean[0];
	        }
	        return result;
		});
	}

	@Override
	public Mono<Boolean> isPermittedAll(Subject subject, String... permissions) {
		return this.loadPermissions(subject).map(ps -> {
			if (CollectionUtils.isEmpty(ps)) {
				return false;
			}
			if (permissions != null && permissions.length != 0) {
	            for (String p : permissions) {
	                if (!ps.contains(p)) {
	                    return false;
	                }
	            }
	        }
	        return true;
		});
	}

	@Override
	public Mono<Boolean> hasRole(Subject subject, String role) {
		return this.loadRoles(subject).map(rs->rs.contains(role));
	}

	@Override
	public Mono<boolean[]> hasRoles(Subject subject, String... roles) {
		return this.loadRoles(subject).map(rs ->{
			boolean[] result;
	        if (roles != null && roles.length != 0) {
	            int size = roles.length;
	            result = new boolean[size];
	            int i = 0;
	            for (String p : roles) {
	                result[i++] = !CollectionUtils.isEmpty(rs) && rs.contains(p);
	            }
	        } else {
	            result = new boolean[0];
	        }
	        return result;
		});
	}

	@Override
	public Mono<Boolean> hasAllRoles(Subject subject, String... roles) {
		return this.loadRoles(subject).map(rs -> {
			if (CollectionUtils.isEmpty(rs)) {
				return false;
			}
			if (roles != null && roles.length != 0) {
	            for (String p : roles) {
	                if (!rs.contains(p)) {
	                    return false;
	                }
	            }
	        }
	        return true;
		});
	}
	
	/**
	 * 加载权限
	 * @param subject
	 * @return
	 */
	private Mono<Set<String>> loadPermissions(Subject subject) {
		Set<String> permissions = subject.getPermissions();
		if (permissions == null) {
			return realm.doGetAuthorizationInfo(subject.getPrincipal()).map(authorization ->{
				subject.setPermissions(authorization.getPermissions());
				subject.setRoles(authorization.getRoles());
				return subject.getPermissions();
			});
		}
		return Mono.just(subject.getPermissions());
	}
	
	/**
	 * 加载角色
	 * @param subject
	 * @return
	 */
	private Mono<Set<String>> loadRoles(Subject subject) {
		Set<String> roles = subject.getRoles();
		if (roles == null) {
			return realm.doGetAuthorizationInfo(subject.getPrincipal()).map(authorization ->{
				subject.setPermissions(authorization.getPermissions());
				subject.setRoles(authorization.getRoles());
				return authorization.getRoles();
			});
		}
		return Mono.just(subject.getRoles());
	}

	@Override
	public Mono<Boolean> login(Subject subject, HttpServerRequest request, HttpServerResponse response) throws AuthenticationException {
		return realm.doAuthentication(request).doOnError((e) ->{
			this.onLoginFailure(request, response);
		}).flatMap(principal ->{
			return this.login(subject, principal, request, response);
		});
	}
	
	/**
	 * 指定了身份来登录
	 */
	@Override
	public Mono<Boolean> login(Subject subject, Principal principal, HttpServerRequest request, HttpServerResponse response) {
		return this.login(subject, principal, true, request, response);
	}
	
	/**
	 * 真实的登录
	 * @param subject
	 * @param principal
	 * @param request
	 * @param response
	 */
	private Mono<Boolean> login(Subject subject, Principal principal, boolean authenticated, HttpServerRequest request, HttpServerResponse response) {
		
		// 设置相关信息到主体中
		subject.setPrincipal(principal);  subject.setAuthenticated(authenticated);
		
		// 创建身份
		return principalStrategy.createPrincipal(subject, request, response).map(s -> false).doOnSuccess((v) ->{
			// 通知登录成功
			this.onLoginSuccess(subject, request, response);
		});
	}

	/**
	 * 退出登录
	 */
	@Override
	public Mono<Boolean> logout(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		return principalStrategy.invalidatePrincipal(subject, request, response).doOnSuccess((v) ->{
			this.onLogout(subject, request, response);
		});
	}

	/**
	 * 获取用户信息
	 */
	@Override
	public Mono<Subject> createSubject(HttpServerRequest request, HttpServerResponse response) {
		return principalStrategy.resolvePrincipal(new FluxSubject(), request, response);
	}
	
	/**
	 * 将此身份失效
	 */
	@Override
	public Mono<Boolean> invalidate(String sessionId, String reason) {
		return principalStrategy.invalidatePrincipal(sessionId).doOnSuccess((v)->{
			realm.onInvalidate(sessionId, reason);
		});
	}
	
	/**
	 * 登录成功
	 * @param subject
	 */
	void onLoginSuccess(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		realm.onLoginSuccess(subject, request);
		eventPublisher.publishSignIn(subject);
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
		eventPublisher.publishLogout(subject);
	}
}