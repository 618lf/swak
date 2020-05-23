package com.swak.vertx.security.handler.impls;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.security.Permission;
import com.swak.vertx.security.handler.PathDefinition;
import com.swak.vertx.transport.HttpConst;
import com.swak.vertx.transport.Subject;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.ext.web.RoutingContext;

/**
 * 需要判断用户拥有什么角色
 *
 * @author: lifeng
 * @date: 2020/3/29 20:39
 */
public class RoleHandler extends PermissionPathDefinitionHandler implements PathDefinition {

    /**
     * 判断用户需要拥有的权限
     */
    @Override
    public CompletionStage<Boolean> isAccessDenied(RoutingContext context, Subject subject) {

        // 获取权限
        Permission permission = this.getPermission(context);

        // 配置了需要的权限
        if (permission != null) {
            return subject.hasRole(permission);
        }

        // 无配置的权限则统一返回 false
        return CompletableFuture.completedFuture(false);
    }

    /**
     * 如果不继续执行则怎么处理
     */
    @Override
    public CompletableFuture<Boolean> onAccessDenied(RoutingContext context, Subject subject) {
        context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
        context.response().end(Result.error(ErrorCode.ACCESS_DENIED).toJson());
        return CompletableFuture.completedFuture(false);
    }
}