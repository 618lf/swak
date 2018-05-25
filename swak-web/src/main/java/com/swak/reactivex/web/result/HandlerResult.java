package com.swak.reactivex.web.result;

/**
 * 处理结果
 * 
 * @author lifeng
 */
public class HandlerResult {

	private Object returnValue;

	public HandlerResult(Object returnValue) {
		this.returnValue = returnValue;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	/**
	 * 获得返回值的类型
	 * 
	 * @return
	 */
	public Class<?> getReturnValueType() {
		if (returnValue != null) {
			return returnValue.getClass();
		}
		return Object.class;
	}
}