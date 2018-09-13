package com.swak.vertx.security.handler.impls;

import java.util.Map;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.vertx.security.Subject;
import com.swak.vertx.security.handler.AdviceHandler;
import com.swak.vertx.security.handler.PathDefinition;
import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.ext.web.RoutingContext;

/**
 * 需要判断用户拥有什么角色
 * 
 * @author lifeng
 */
public class RoleHandler extends AdviceHandler implements PathDefinition {

	private Map<String, String> params = Maps.newHashMap();
	
	/**
	 * 判断用户需要拥有的权限
	 * 
	 * @param subject
	 * @return
	 */
	public boolean isAccessDenied(RoutingContext context, Subject subject) {
		
		// 获取参数
		String param = params.get(context.get(CHAIN_RESOLVE_PATH));
		
		// 配置了需要的权限
		if (StringUtils.isNotBlank(param)) {
			String roles = subject.getRoles();
			return StringUtils.isNotBlank(roles) && StringUtils.contains(roles, param.toString());
		}
		
		// 无配置的权限则统一返回 false
		return false;
	}

	/**
	 * 如果不继续执行则怎么处理
	 * 
	 * @param subject
	 * @return
	 */
	public boolean onAccessDenied(RoutingContext context, Subject subject) {
		context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		context.response().end(Result.error(ErrorCode.ACCESS_DENIED).toJson());
		return false;
	}

	/**
	 * 路径的配置
	 */
	@Override
	public void pathConfig(String path, String param) {
		params.put(path, param);
	}
}