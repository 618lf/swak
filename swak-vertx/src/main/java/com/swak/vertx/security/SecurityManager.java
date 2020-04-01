package com.swak.vertx.security;

import com.swak.security.Permission;
import com.swak.vertx.security.principal.PrincipalStrategy;
import com.swak.vertx.security.realm.Realm;
import com.swak.vertx.transport.Subject;
import com.swak.vertx.transport.Token;
import io.vertx.ext.web.RoutingContext;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 安全管理的基本定義
 *
 * @author: lifeng
 * @date: 2020/3/29 20:50
 */
public class SecurityManager {

    private final PrincipalStrategy strategy;
    private final Realm realm;

    /**
     * 配置基本的依赖
     *
     * @param strategy 身份管理策略
     * @param realm    身份获取域
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
     */
    public CompletionStage<Token> login(Subject subject, RoutingContext context) {
        return this.realm.onLogin(subject).thenCompose(res -> this.strategy.generateToken(subject));
    }

    // ---------- 权限校验部分（-）通过配置以及注解设置的权限验证方式会被封装为 Permission 对象 -------------

    /**
     *
     */
    public CompletionStage<Boolean> isPermitted(Subject subject, Permission permission) {
        return this.loadPermissions(subject).thenApply(permission::implies);
    }

    public CompletionStage<Boolean> hasRole(Subject subject, Permission permission) {
        return this.loadRoles(subject).thenApply(permission::implies);
    }

    // ---------- 权限校验部分（二）也可以在代码中手动调用如下验证方式 -------------

    /**
     *
     */
    public CompletionStage<Boolean> isPermitted(Subject subject, String permission) {
        return this.loadPermissions(subject).thenApply(ps -> ps.contains(permission));
    }

    public CompletionStage<boolean[]> isPermitted(Subject subject, String... permissions) {
        return this.loadPermissions(subject).thenApply(ps -> this.checks(ps, permissions));
    }

    public CompletionStage<Boolean> isPermittedAll(Subject subject, String... permissions) {
        return this.loadPermissions(subject).thenApply(ps -> this.checkAll(ps, permissions));
    }

    public CompletionStage<Boolean> hasRole(Subject subject, String role) {
        return this.loadRoles(subject).thenApply(rs -> rs.contains(role));
    }

    public CompletionStage<boolean[]> hasRoles(Subject subject, String... roles) {
        return this.loadRoles(subject).thenApply(ps -> this.checks(ps, roles));
    }

    public CompletionStage<Boolean> hasAllRoles(Subject subject, String... roles) {
        return this.loadRoles(subject).thenApply(rs -> this.checkAll(rs, roles));
    }

    /**
     * 加载权限
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

    /**
     * 校验是否全部包含
     */
    private boolean[] checks(Set<String> sources, String... targets) {
        boolean[] result;
        if (targets != null && targets.length != 0) {
            int size = targets.length;
            result = new boolean[size];
            int i = 0;
            for (String p : targets) {
                result[i++] = !CollectionUtils.isEmpty(sources) && sources.contains(p);
            }
        } else {
            result = new boolean[0];
        }
        return result;
    }

    /**
     * 校验是否全部包含
     */
    private boolean checkAll(Set<String> sources, String... targets) {
        if (CollectionUtils.isEmpty(sources)) {
            return false;
        }
        if (targets != null && targets.length != 0) {
            for (String p : targets) {
                if (!sources.contains(p)) {
                    return false;
                }
            }
        }
        return true;
    }
}
