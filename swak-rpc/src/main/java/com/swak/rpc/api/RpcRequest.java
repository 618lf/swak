package com.swak.rpc.api;

import java.io.Serializable;

import com.swak.reactivex.transport.NettyInbound;

/**
 * 封装 RPC 请求
 * 
 * @author lifeng
 */
public class RpcRequest implements NettyInbound, Serializable {

	private static final long serialVersionUID = 1L;
	private String requestId;
	private String serviceName;
	private String version;
	private String group;
	private String methodName;
	private String[] parameterTypes;
	private Object[] parameters;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
}
