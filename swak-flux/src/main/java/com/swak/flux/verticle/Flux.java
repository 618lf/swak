package com.swak.flux.verticle;

import java.util.concurrent.CompletableFuture;

import com.swak.flux.verticle.FluxImpl.DeploymentOptions;

/**
 * 定义 消息驱动对象
 * 
 * @author lifeng
 */
public interface Flux {

	/**
	 * 发布
	 */
	void deployment(Verticle verticle);

	/**
	 * 发布
	 */
	void deployment(Verticle verticle, DeploymentOptions options);

	/**
	 * 发送消息
	 * 
	 * @param address
	 * @param request
	 * @param timeout
	 * @param handler
	 */
	CompletableFuture<Msg> sendMessage(String address, Msg request);
	
	/**
	 * 关闭
	 */
	void close();
}