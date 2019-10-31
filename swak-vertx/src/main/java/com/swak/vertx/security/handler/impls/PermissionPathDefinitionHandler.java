package com.swak.vertx.security.handler.impls;

import java.util.Map;

import com.swak.Constants;
import com.swak.security.Permission;
import com.swak.security.permission.AndPermission;
import com.swak.security.permission.OrPermission;
import com.swak.security.permission.Permissions;
import com.swak.security.permission.SinglePermission;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.vertx.security.handler.AdviceHandler;
import com.swak.vertx.security.handler.PathDefinition;

import io.vertx.ext.web.RoutingContext;

/**
 * 基于 Permission 的权限定义方式
 * 
 * @author lifeng
 */
public abstract class PermissionPathDefinitionHandler extends AdviceHandler implements PathDefinition {

	private Map<String, Permission> params = Maps.newHashMap();

	/**
	 * 权限配置方式(不能混合使用):<br>
	 * 
	 * 1: a,b,c <br>
	 * 2: a|b|c <br>
	 */
	@Override
	public void pathConfig(String path, String param) {
		
		// 不支持空权限校验
		if (StringUtils.isBlank(param)) {
			return;
		}

		// 权限持有
		Permission permission = null;

		// and 权限集合的处理方式
		if (StringUtils.contains(param, Constants.security_AND_DIVIDER_TOKEN)) {
			String[] permissions = param.split(Constants.security_AND_DIVIDER_TOKEN);
			permission = new AndPermission(permissions);
		}
		// or 权限集合的处理方式
		else if (StringUtils.contains(param, Constants.security_OR_DIVIDER_TOKEN)) {
			String[] permissions = param.split(Constants.security_AND_DIVIDER_TOKEN);
			permission = new OrPermission(permissions);
		} else {
			permission = new SinglePermission(param);
		}

		// 创建权限链
		Permission holder = params.get(path);
		if (holder == null) {
			params.put(path, permission);
		} else if (holder instanceof Permissions) {
			((Permissions) holder).addPermission(permission);
		} else {
			params.put(path, new Permissions().addPermission(holder).addPermission(permission));
		}
	}
	
	/**
	 * 获取需要处理的权限验证鏈
	 * 
	 * @param context
	 * @return
	 */
	protected Permission getPermission(RoutingContext context) {
		return params.get(context.get(CHAIN_RESOLVE_PATH));
	}
}