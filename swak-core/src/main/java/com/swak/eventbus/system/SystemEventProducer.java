package com.swak.eventbus.system;

import com.swak.Constants;
import com.swak.eventbus.Event;
import com.swak.eventbus.EventProducer;

/**
 * 基于 redis 的 事件发布器
 * @author lifeng
 */
public class SystemEventProducer implements SystemEventPublisher {

	private final EventProducer eventProducer;
	
	public SystemEventProducer(EventProducer eventProducer) {
		this.eventProducer = eventProducer;
	}
	
	@Override
	public void publishError(Throwable t) {
		eventProducer.publish(Constants.SYSTEM_EVENT_TOPIC, new Event(t));
	}

	@Override
	public void publishSignIn(Object subject) {
		eventProducer.publish(Constants.SYSTEM_EVENT_TOPIC, Event.msg(subject).type(Constants.SIGN_IN));
	}

	@Override
	public void publishSignUp(Object subject) {
		eventProducer.publish(Constants.SYSTEM_EVENT_TOPIC, Event.msg(subject).type(Constants.SIGN_UP));
	}
	
	@Override
	public void publishLogout(Object subject) {
		eventProducer.publish(Constants.SYSTEM_EVENT_TOPIC, Event.msg(subject).type(Constants.LOGOUT));
	}
}