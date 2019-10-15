package com.swak.vertx.security.realm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.vertx.transport.AuthorizationInfo;
import com.swak.vertx.transport.Principal;

/**
 * 空 权限校验
 * 
 * @author lifeng
 */
public class SimpleRealm implements Realm {

	@Override
	public CompletionStage<AuthorizationInfo> doGetAuthorizationInfo(Principal principal) {
		return CompletableFuture.completedFuture(null);
	}
}
