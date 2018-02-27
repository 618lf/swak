package com.tmt.concurrent;

public class DateTestFactory extends FutureProxy<DateTest>{

	@Override
	protected DateTest createInstance() {
		return new DateTestImpl();
	}

	@Override
	protected Class<? extends DateTest> getInterface() {
		return DateTest.class;
	}
}
