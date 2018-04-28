package com.swak.reactivex.web.method;

/**
 * 处理结果
 * @author lifeng
 */
public class HandlerResult {
	
	private HandlerMethod handler;
	private Object returnValue;
	
	public HandlerResult(HandlerMethod handler, Object returnValue) {
		this.handler = handler;
		this.returnValue = returnValue;
	}
	
	public HandlerMethod getHandler() {
		return handler;
	}
	public void setHandler(HandlerMethod handler) {
		this.handler = handler;
	}
	public Object getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}
}
