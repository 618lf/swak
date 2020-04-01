package com.swak.vertx.security.realm;

import com.swak.vertx.transport.AuthorizationInfo;
import com.swak.vertx.transport.Subject;

import java.util.concurrent.CompletionStage;

/**
 * 获取权限域信息
 *
 * @author: lifeng
 * @date: 2020/3/29 20:45
 */
public interface Realm {

    /**
     * 获取当前身份的权限信息
     *
     * @param subject 身份信息
     * @return 权限信息
     */
    CompletionStage<AuthorizationInfo> doGetAuthorizationInfo(Subject subject);

    /**
     * 登录事件
     *
     * @param subject 身份信息
     * @return 异步
     */
    CompletionStage<Void> onLogin(Subject subject);
}