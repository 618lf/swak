package com.swak.paxos.event;

import com.swak.paxos.protol.Propoal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 事件
 * 
 * @author DELL
 */
@Getter
@Setter
@Accessors(chain = true)
public class Event {
	public static final byte Event_Propose = 1;
	public static final byte Event_Paxos = 2;

	private byte type;
	private Object event;
	private EventLoop loop;

	@SuppressWarnings("unchecked")
	public <T> T getEvent() {
		return (T) event;
	}

	public static Event Propose(EventLoop loop, Propoal event) {
		Event e = new Event();
		e.loop = loop;
		e.type = Event_Propose;
		e.event = event;
		return e;
	}
}
