package com.swak.reliable;

import com.swak.exception.BaseRuntimeException;

public class ReliableMessageException extends BaseRuntimeException {

	public ReliableMessageException(String msg) {
		super(msg);
	}
	private static final long serialVersionUID = 1L;

}
