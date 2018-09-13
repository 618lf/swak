package com.swak.vertx.security.handler;

import com.swak.vertx.security.Subject;

import io.vertx.ext.web.RoutingContext;

/**
 * 访问控制器
 * 
 * @author lifeng
 */
public abstract class AdviceHandler implements Handler {

	/**
	 * 执行处理
	 * 
	 * @param subject
	 * @return
	 */
	public boolean handle(RoutingContext context, Subject subject, HandlerChain chain) {
		// 执行判断处理
		boolean continued = this.isAccessDenied(context, subject) || this.onAccessDenied(context, subject);
		if (continued) {
			return chain.doHandler(context, subject);
		}
		return false;
	}

	/**
	 * 是否继续执行
	 * 
	 * @param subject
	 * @return
	 */
	public boolean isAccessDenied(RoutingContext context, Subject subject) {
		return true;
	}

	/**
	 * 如果不继续执行则怎么处理
	 * 
	 * @param subject
	 * @return
	 */
	public boolean onAccessDenied(RoutingContext context, Subject subject) {
		return true;
	}
}
