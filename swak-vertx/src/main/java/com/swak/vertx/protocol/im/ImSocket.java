package com.swak.vertx.protocol.im;

import java.util.concurrent.CompletableFuture;

/**
 * 代理接口
 * 
 * @author lifeng
 * @date 2020年10月23日 上午11:31:12
 */
public interface ImSocket {

	
	/**
	 * 远端地址
	 * 
	 * @return
	 */
	String remoteAddress();

	/**
	 * 本地地址
	 * 
	 * @return
	 */
	String localAddress();

	/**
	 * 发送消息
	 * 
	 * @param text
	 * @return
	 */
	void sendText(String text);

	/**
	 * 异步发送消息
	 * 
	 * @param text
	 * @return
	 */
	CompletableFuture<Void> sendTextAsync(String text);
}
