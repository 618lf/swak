package com.swak.vertx.config;

import io.vertx.ext.web.Router;

/**
 * 
 * 路由配置项目 
 * 
 * @author lifeng
 */
public interface IRouterConfig {

	/**
	 * 对 router 进行配置
	 * @param router
	 */
	void apply(Router router);
}
