package com.swak.vertx.security.realm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.vertx.transport.AuthorizationInfo;
import com.swak.vertx.transport.Subject;

/**
 * 空 权限校验
 *
 * @author: lifeng
 * @date: 2020/3/29 20:46
 */
public class SimpleRealm implements Realm {

    @Override
    public CompletionStage<AuthorizationInfo> doGetAuthorizationInfo(Subject subject) {
        return CompletableFuture.completedFuture(new AuthorizationInfo());
    }

    @Override
    public CompletionStage<Void> onLogin(Subject subject) {
        return CompletableFuture.completedFuture(null);
    }
}