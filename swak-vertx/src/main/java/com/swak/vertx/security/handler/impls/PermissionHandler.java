package com.swak.vertx.security.handler.impls;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.vertx.security.handler.AdviceHandler;
import com.swak.vertx.security.handler.PathDefinition;
import com.swak.vertx.transport.HttpConst;
import com.swak.vertx.transport.Subject;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.ext.web.RoutingContext;

/**
 * 权限校验
 * 
 * @author lifeng
 */
public class PermissionHandler extends AdviceHandler implements PathDefinition {

	private Map<String, String> params = Maps.newHashMap();

	/**
	 * 判断用户需要拥有的权限
	 * 
	 * @param subject
	 * @return
	 */
	public CompletionStage<Boolean> isAccessDenied(RoutingContext context, Subject subject) {

		// 获取参数
		String param = params.get(context.get(CHAIN_RESOLVE_PATH));

		// 配置了需要的权限
		if (StringUtils.isNotBlank(param)) {
			return subject.isPermitted(param);
		}

		// 无配置的权限则统一返回 false
		return CompletableFuture.completedFuture(false);
	}

	/**
	 * 如果不继续执行则怎么处理
	 * 
	 * @param subject
	 * @return
	 */
	public CompletableFuture<Boolean> onAccessDenied(RoutingContext context, Subject subject) {
		context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		context.response().end(Result.error(ErrorCode.ACCESS_DENIED).toJson());
		return CompletableFuture.completedFuture(false);
	}

	/**
	 * 路径的配置
	 */
	@Override
	public void pathConfig(String path, String param) {
		params.put(path, param);
	}
}