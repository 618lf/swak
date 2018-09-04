package com.swak.vertx.handler;

import java.util.function.Consumer;

import com.swak.vertx.handler.codec.Msg;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

/**
 * 代理处理 vertx 的 相关服务
 * 
 * @author lifeng
 */
public interface VertxHandler {

	
	/**
	 * 应用
	 * @param apply
	 */
	void apply(Consumer<Vertx> apply);
	
	/**
	 * 销毁
	 */
	void destroy(Consumer<Vertx> apply);
	
	/**
	 * 发送消息
	 * @param address
	 * @param request
	 */
	void sentMessage(String address, Msg request);
	
	/**
	 * 发送消息
	 * @param address
	 * @param request
	 */
	void sentMessage(String address, Msg request, Handler<AsyncResult<Message<Msg>>> replyHandler);
}