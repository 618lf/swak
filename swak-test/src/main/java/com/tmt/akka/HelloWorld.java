package com.tmt.akka;

import java.util.Arrays;

import com.swak.common.utils.JsonMapper;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

public class HelloWorld extends UntypedAbstractActor {

	@Override
	public void preStart() {
		// create the greeter actor
		final ActorRef greeter = getContext().actorOf(Props.create(Greeter.class), "greeter");
		// tell it to perform the greeting
		greeter.tell(new Message(2, Arrays.asList("2", "dsf")) , getSelf());
	}

	@Override
	public void onReceive(Object msg) throws InterruptedException {
		System.out.println(JsonMapper.toJson(msg));
	}
}
