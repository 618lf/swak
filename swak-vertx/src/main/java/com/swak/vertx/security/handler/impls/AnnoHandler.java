package com.swak.vertx.security.handler.impls;

import com.swak.vertx.security.Subject;
import com.swak.vertx.security.handler.AdviceHandler;

import io.vertx.ext.web.RoutingContext;

/**
 * 匿名处理器
 * 
 * @author lifeng
 *
 */
public class AnnoHandler extends AdviceHandler {

	/**
	 * 什么都不用判断，继续执行
	 * 
	 * @param subject
	 * @return
	 */
	public boolean isAccessDenied(RoutingContext context, Subject subject) {
		return true;
	}
}