package com.tmt.akka;

import com.swak.common.utils.JsonMapper;

import akka.actor.UntypedAbstractActor;

public class Greeter extends UntypedAbstractActor {

	@Override
	public void onReceive(Object msg) throws InterruptedException {
		System.out.println("Greeter收到的数据为：" + JsonMapper.toJson(msg));
		getSender().tell("Greeter工作完成。", getSelf());// 给发送至发送信息.
	}
}
