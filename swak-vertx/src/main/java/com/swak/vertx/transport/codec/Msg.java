package com.swak.vertx.transport.codec;

import java.io.Serializable;

import com.swak.asm.MethodCache.MethodMeta;

/**
 * 定义传递的消息
 *
 * @author: lifeng
 * @date: 2020/3/29 21:10
 */
public class Msg implements Serializable {

    private static final long serialVersionUID = 1L;

    private String methodName;
    private String methodDesc;
    private Object[] arguments;
    private Object result;
    private Throwable error;

    public Msg() {

    }

    public Msg(MethodMeta meta, Object[] arguments) {
        this.methodName = meta.getMethodName();
        this.methodDesc = meta.getMethodDesc();
        this.arguments = arguments;
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

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
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