package com.tmt.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class TestMain {

	public static void main(String[] args) {
		 ActorSystem system = ActorSystem.create("Hello");
		 ActorRef a = system.actorOf(Props.create(HelloWorld.class), "helloWorld");
		 System.out.println(a.path());
	}

}
