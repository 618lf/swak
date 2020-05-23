package com.swak.vertx.dsl;

import io.vertx.ext.web.Router;

/**
 * 路由处理器
 * 
 * @author lifeng
 */
@FunctionalInterface
public interface RouterFunction {

	/**
	 * 返回路由
	 * 
	 * @return
	 */
	void route(Router router);
}
