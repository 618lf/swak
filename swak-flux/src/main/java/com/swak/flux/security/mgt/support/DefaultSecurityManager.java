package com.swak.flux.security.mgt.support;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.util.CollectionUtils;

import com.swak.flux.security.FluxSubject;
import com.swak.flux.security.exception.AuthenticationException;
import com.swak.flux.security.mgt.SecurityManager;
import com.swak.flux.security.principal.PrincipalStrategy;
import com.swak.flux.security.realm.Realm;
import com.swak.flux.transport.Principal;
import com.swak.flux.transport.Subject;
import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 默认的安全管理器
 * 
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
	public CompletionStage<Boolean> isPermitted(Subject subject, String permission) {
		return this.loadPermissions(subject).thenApply(ps -> ps.contains(permission));
	}

	@Override
	public CompletionStage<boolean[]> isPermitted(Subject subject, String... permissions) {
		return this.loadPermissions(subject).thenApply(ps -> {
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
	public CompletionStage<Boolean> isPermittedAll(Subject subject, String... permissions) {
		return this.loadPermissions(subject).thenApply(ps -> {
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
	public CompletionStage<Boolean> hasRole(Subject subject, String role) {
		return this.loadRoles(subject).thenApply(rs -> rs.contains(role));
	}

	@Override
	public CompletionStage<boolean[]> hasRoles(Subject subject, String... roles) {
		return this.loadRoles(subject).thenApply(rs -> {
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
	public CompletionStage<Boolean> hasAllRoles(Subject subject, String... roles) {
		return this.loadRoles(subject).thenApply(rs -> {
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
	 * 
	 * @param subject
	 * @return
	 */
	private CompletionStage<Set<String>> loadPermissions(Subject subject) {
		Set<String> permissions = subject.getPermissions();
		if (permissions == null) {
			return realm.doGetAuthorizationInfo(subject.getPrincipal()).thenApply(authorization -> {
				subject.setPermissions(authorization.getPermissions());
				subject.setRoles(authorization.getRoles());
				return subject.getPermissions();
			});
		}
		return CompletableFuture.completedFuture(subject.getPermissions());
	}

	/**
	 * 加载角色
	 * 
	 * @param subject
	 * @return
	 */
	private CompletionStage<Set<String>> loadRoles(Subject subject) {
		Set<String> roles = subject.getRoles();
		if (roles == null) {
			return realm.doGetAuthorizationInfo(subject.getPrincipal()).thenApply(authorization -> {
				subject.setPermissions(authorization.getPermissions());
				subject.setRoles(authorization.getRoles());
				return authorization.getRoles();
			});
		}
		return CompletableFuture.completedFuture(subject.getRoles());
	}

	@Override
	public Mono<Boolean> login(Subject subject, HttpServerRequest request, HttpServerResponse response)
			throws AuthenticationException {
		return Mono.fromCompletionStage(realm.doAuthentication(request)).doOnError((e) -> {
			this.onLoginFailure(request, response);
		}).flatMap(principal -> {
			return this.login(subject, principal, request, response);
		});
	}

	/**
	 * 指定了身份来登录
	 */
	@Override
	public Mono<Boolean> login(Subject subject, Principal principal, HttpServerRequest request,
			HttpServerResponse response) {
		return this.login(subject, principal, true, request, response);
	}

	/**
	 * 真实的登录
	 * 
	 * @param subject
	 * @param principal
	 * @param request
	 * @param response
	 */
	private Mono<Boolean> login(Subject subject, Principal principal, boolean authenticated, HttpServerRequest request,
			HttpServerResponse response) {

		// 设置相关信息到主体中
		subject.setPrincipal(principal);
		subject.setAuthenticated(authenticated);

		// 创建身份
		return principalStrategy.createPrincipal(subject, request, response).map(s -> false).doOnSuccess((v) -> {
			// 通知登录成功
			this.onLoginSuccess(subject, request, response);
		});
	}

	/**
	 * 退出登录
	 */
	@Override
	public Mono<Boolean> logout(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		return principalStrategy.invalidatePrincipal(subject, request, response).doOnSuccess((v) -> {
			this.onLogout(subject, request, response);
		});
	}

	/**
	 * 获取用户信息
	 */
	@Override
	public Mono<Subject> createSubject(HttpServerRequest request, HttpServerResponse response) {
		
		// 创建一个默认的 subject
		Subject subject = new FluxSubject();
		
		// 设置到当前的 request 中
		request.setSubject(subject);
		
		// 获取身份
		return principalStrategy.resolvePrincipal(subject, request, response);
	}

	/**
	 * 登录成功
	 * 
	 * @param subject
	 */
	void onLoginSuccess(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		realm.onLoginSuccess(subject, request);
	}

	/**
	 * 登录失败
	 * 
	 * @param request
	 */
	void onLoginFailure(HttpServerRequest request, HttpServerResponse response) {
		realm.onLoginFailure(request);
	}

	/**
	 * 退出登录
	 */
	void onLogout(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		realm.onLogout(subject);
	}
}