package com.tmt.reactor.b;

import java.util.List;

import com.google.common.collect.Lists;

public class SomeFeed {

	List<SomeListener> listeners = Lists.newArrayList();

	public void register(SomeListener listener) {
		listeners.add(listener);
	}

	public SomeFeed publish(PriceTick event) {
		listeners.stream().forEach(listener -> listener.priceTick(event));
		return this;
	}
}
