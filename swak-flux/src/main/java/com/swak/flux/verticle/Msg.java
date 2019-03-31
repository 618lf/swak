package com.swak.flux.verticle;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import com.swak.asm.MethodCache.MethodMeta;

/**
 * 定义传递的消息, 默认1s的等待时间
 * 
 * @author lifeng
 */
public class Msg implements Serializable {

	private static final long serialVersionUID = 1L;

	private String methodName;
	private String methodDesc;
	private Object[] arguments;
	private Object result;
	private String error;
	private int timeOut;
	private long createtime;
	private CompletableFuture<Msg> future;

	public Msg(MethodMeta meta, Object[] arguments) {
		this.timeOut = meta.getTimeOut() <= 0 ? 1000 : meta.getTimeOut();
		this.methodName = meta.getMethodName();
		this.methodDesc = meta.getMethodDesc();
		this.arguments = arguments;
		this.createtime = System.currentTimeMillis();
	}

	public int getTimeOut() {
		return timeOut;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@SuppressWarnings("unchecked")
	public <T> T getResult() {
		return (T) result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public String getMethodDesc() {
		return methodDesc;
	}

	public void setMethodDesc(String methodDesc) {
		this.methodDesc = methodDesc;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public CompletableFuture<Msg> getFuture() {
		return future;
	}

	public Msg setFuture(CompletableFuture<Msg> future) {
		this.future = future;
		return this;
	}

	public void cancel() {
		Exception cause = new RuntimeException("Execute timeout");
		this.cancel(cause);
	}

	public void cancel(Exception cause) {
		if (this.future != null) {
			this.future.completeExceptionally(cause);
		}
	}

	public Msg reset() {
		this.methodDesc = null;
		this.methodName = null;
		this.arguments = null;
		this.result = null;
		this.error = null;
		return this;
	}
}