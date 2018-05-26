package com.swak.http.reactor.sink;

import java.util.concurrent.CancellationException;

public class DisposedException extends CancellationException {
	private static final long serialVersionUID = -5885577182105850384L;

	public DisposedException(String message) {
		super(message);
	}
}
