package com.swak.rpc.protocol;

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
	private String interfaceName;
	private String version;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String className) {
		this.interfaceName = className;
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

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
}
