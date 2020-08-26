package com.swak.vertx.protocol.im;

import com.swak.vertx.invoker.MethodInvoker;

/**
 * WebSocket 的处理器
 * 
 * @author lifeng
 * @date 2020年4月25日 下午6:02:22
 */
public interface ImHandler {

	/**
	 * 路由处理器
	 *
	 * @param context 请求上下文
	 * @param handler 处理器
	 */
	void handle(ImContext context, MethodInvoker handler);
}