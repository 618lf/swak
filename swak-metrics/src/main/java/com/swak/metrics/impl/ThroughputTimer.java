package com.swak.metrics.impl;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Timer;

import io.vertx.ext.dropwizard.impl.InstantThroughput;

public class ThroughputTimer extends Timer {

	private final InstantThroughput instantThroughput = new InstantThroughput();

	public Long getValue() {
		return instantThroughput.count();
	}

	@Override
	public void update(long duration, TimeUnit unit) {
		super.update(duration, unit);
		instantThroughput.mark();
	}
}