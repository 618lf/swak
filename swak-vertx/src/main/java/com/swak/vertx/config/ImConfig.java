package com.swak.vertx.config;

import com.swak.vertx.protocol.im.ImRouter;

import io.vertx.core.Vertx;

/**
 * ImConfig 的配置
 * 
 * @author lifeng
 * @date 2020年8月27日 上午9:44:32
 */
public interface ImConfig extends AbstractConfig {

	/**
	 * 对 ImRouter 进行配置
	 *
	 * @param vertx  代理vertx
	 * @param router router
	 */
	void apply(Vertx vertx, ImRouter router);
}
