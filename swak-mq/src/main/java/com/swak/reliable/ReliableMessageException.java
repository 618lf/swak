package com.swak.reliable;

import com.swak.exception.BaseRuntimeException;

public class ReliableMessageException extends BaseRuntimeException {

	public ReliableMessageException(String msg) {
		super(msg);
	}

	public ReliableMessageException(String msg, Throwable e) {
		super(msg, e);
	}

	private static final long serialVersionUID = 1L;

}
