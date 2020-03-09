package com.swak.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

/**
 * 发送端的处理器
 * 
 * @author lifeng
 */
public class SendHandler implements Handler<AsyncResult<Message<Object>>> {

	@Override
	public void handle(AsyncResult<Message<Object>> event) {
		System.out.println("收到执行结果");
	}
}
