package com.swak.vertx.security.handler.impls;

import java.util.List;
import java.util.Map;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.utils.Lists;
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

	private Map<String, Object> params = Maps.newHashMap();

	/**
	 * 判断用户需要拥有的权限
	 * 
	 * @param subject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isAccessDenied(RoutingContext context, Subject subject) {

		// 获取参数
		Object param = params.get(context.get(CHAIN_RESOLVE_PATH));

		// 单个权限
		if (param != null && param instanceof String) {
			return this.checkRole(subject, param.toString());
		} else if (param != null && param instanceof List) {
			List<String> roles = (List<String>) param;
			for (String role : roles) {
				if (this.checkRole(subject, role)) {
					return true;
				}
			}
		}

		// 无配置的权限则统一返回 false
		return false;
	}

	/**
	 * 目前使用这种简单的验证方式
	 * 
	 * @param subject
	 * @param role
	 * @return
	 */
	private boolean checkRole(Subject subject, String role) {
		String roles = subject.getRoles();
		return StringUtils.isNotBlank(roles) && StringUtils.contains(roles, role);
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
	 * 暂时简单的实现： 路径的配置 <br>
	 * a|b a权限或b权限 <br>
	 * a,b a权限和b权限 <br>
	 */
	@Override
	public void pathConfig(String path, String param) {
		List<String> orRoles = Lists.newArrayList();
		if (StringUtils.contains(param, "|")) {
			String[] _params = StringUtils.split("|");
			orRoles = Lists.newArrayList(_params);
			params.put(path, orRoles);
		} else {
			params.put(path, param);
		}
	}
}