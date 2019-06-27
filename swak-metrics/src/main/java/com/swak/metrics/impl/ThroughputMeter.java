package com.swak.metrics.impl;

import com.codahale.metrics.Meter;

public class ThroughputMeter extends Meter {
	private final InstantThroughput instantThroughput = new InstantThroughput();

	public Long getValue() {
		return instantThroughput.count();
	}

	@Override
	public void mark() {
		super.mark();
		instantThroughput.mark();
	}
}
