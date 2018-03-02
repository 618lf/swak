package com.tmt.queue;

public class WorkerEventFactory implements com.lmax.disruptor.EventFactory<Event>{

	@Override
	public Event newInstance() {
		return new Event();
	}
}
