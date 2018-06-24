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
	private String protocol;
	private String serviceName;
	private String version;
	private String group;
	private String methodName;
	private Class<?>[] parameterTypes;
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
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * 直接暴露接口名称，省不少事情
	 * 例如： swak://com.tmt.shop.ShopService
	 * @return
	 */
	public String getServiceKey() {
		StringBuilder buf = new StringBuilder();
		if (protocol != null && protocol.length() > 0) {
			buf.append(protocol);
			buf.append("://");
		}
		String path = getServiceName();
		if (path != null && path.length() > 0) {
			buf.append("/");
			buf.append(path);
		}
		return buf.toString();
	}
}
