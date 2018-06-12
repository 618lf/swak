package com.swak.rpc.api;

import java.io.Serializable;

import com.swak.reactivex.transport.NettyOutbound;

/**
 * 封装 RPC 响应
 * @author lifeng
 */
public class RpcResponse implements NettyOutbound, Serializable{

	private static final long serialVersionUID = 1L;
	
	private String requestId;
    private Throwable exception;
    private Object result;

    public boolean hasException() {
        return exception != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
