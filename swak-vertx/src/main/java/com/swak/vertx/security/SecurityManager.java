package com.swak.vertx.security;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.util.CollectionUtils;

import com.swak.vertx.security.principal.PrincipalStrategy;
import com.swak.vertx.security.realm.Realm;
import com.swak.vertx.transport.Subject;
import com.swak.vertx.transport.Token;

import io.vertx.ext.web.RoutingContext;

/**
 * 安全管理的基本定義
 * 
 * @author lifeng
 */
public class SecurityManager {

	private final PrincipalStrategy strategy;
	private final Realm realm;

	/**
	 * 配置基本的依赖
	 * 
	 * @param jwtAuthProvider
	 * @param realm
	 */
	public SecurityManager(PrincipalStrategy strategy, Realm realm) {
		this.strategy = strategy;
		this.realm = realm;
	}

	// ------------ 主体部分 ---------------

	public CompletionStage<Subject> createSubject(RoutingContext context) {
		return strategy.createSubject(context);
	}

	/**
	 * 登录
	 * 
	 * @param token
	 */
	public CompletionStage<Token> login(Subject subject, RoutingContext context) {
		return this.realm.onLogin(subject).thenCompose(res -> {
			return this.strategy.generateToken(subject);
		});
	}

	// ---------- 权限校验部分 -------------

	public CompletionStage<Boolean> isPermitted(Subject subject, String permission) {
		return this.loadPermissions(subject).thenApply(ps -> ps.contains(permission));
	}

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

	public CompletionStage<Boolean> hasRole(Subject subject, String role) {
		return this.loadRoles(subject).thenApply(rs -> rs.contains(role));
	}

	@Deprecated
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
			return realm.doGetAuthorizationInfo(subject).thenApply(authorization -> {
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
			return realm.doGetAuthorizationInfo(subject).thenApply(authorization -> {
				subject.setPermissions(authorization.getPermissions());
				subject.setRoles(authorization.getRoles());
				return authorization.getRoles();
			});
		}
		return CompletableFuture.completedFuture(subject.getRoles());
	}
}
