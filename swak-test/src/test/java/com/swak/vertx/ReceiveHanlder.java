package com.swak.vertx;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

public class ReceiveHanlder implements Handler<Message<Object>> {

	@Override
	public void handle(Message<Object> event) {
		System.out.println("收到请求 -- 处理结果");
		event.reply("返回结果");
	}
}