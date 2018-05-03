package com.swak.reactivex.web.result;

import com.swak.reactivex.web.Handler;
import com.swak.reactivex.web.function.HandlerFunction;
import com.swak.reactivex.web.method.HandlerMethod;

/**
 * 处理结果
 * @author lifeng
 */
public class HandlerResult {
	
	private Handler handler;
	private Object returnValue;
	
	public HandlerResult(Handler handler, Object returnValue) {
		this.handler = handler;
		this.returnValue = returnValue;
	}
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	public Object getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}
	
	/**
	 * 获得返回值的类型
	 * @return
	 */
	public Class<?> getReturnValueType() {
	   if (handler instanceof HandlerMethod) {
		   return ((HandlerMethod)handler).getReturnValue().getNestedParameterType();
	   } else if(handler instanceof HandlerFunction) {
		   return null;
	   } else if(returnValue != null) {
		   return returnValue.getClass();
	   }
	   return null;
	}
}