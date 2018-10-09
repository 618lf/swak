package com.swak.vertx.handler;

import java.util.function.Consumer;

import com.swak.vertx.transport.codec.Msg;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;

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
	void sentMessage(String address, Msg request, int timeout);
	
	/**
	 * 发送消息
	 * @param address
	 * @param request
	 */
	void sentMessage(String address, Msg request, int timeout, Handler<AsyncResult<Message<Msg>>> replyHandler);
	
	/**
	 * 文件系统
	 * @return
	 */
	FileSystem fileSystem();
}