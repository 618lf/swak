package com.swak.rpc.invoker;

/**
 * 执行异常
 * @author lifeng
 */
public class InvokeException extends RuntimeException{

	private static final long serialVersionUID = 1L;
 
	public InvokeException(String message) {
		super(message);
	}
	public InvokeException(Exception e) {
		super(e);
	}
}