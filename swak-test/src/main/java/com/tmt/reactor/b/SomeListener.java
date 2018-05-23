package com.tmt.reactor.b;

public interface SomeListener {
	void priceTick(PriceTick event);

	void error(Throwable e);
}
