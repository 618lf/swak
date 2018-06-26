package com.swak.rpc.protocol.swak;

import com.swak.rpc.api.Constants;
import com.swak.rpc.api.URL;
import com.swak.rpc.exception.RpcException;
import com.swak.rpc.invoker.Invoker;
import com.swak.rpc.invoker.RemoteInvoker;
import com.swak.rpc.protocol.DefaultExporter;
import com.swak.rpc.protocol.Exporter;
import com.swak.rpc.protocol.Protocol;

/**
 * 简单的二进制协议
 * @author lifeng
 */
public class SwakProtocol implements Protocol {
	
	// 只能为服务器所用
	private String host;
	private int port;
	
	public SwakProtocol() {}
	
	public SwakProtocol(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * 转为 Exporter
	 */
	@Override
	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		URL invokerUrl = invoker.getURL();
		return new DefaultExporter<T>(new URL(Constants.PROTOCOL, host, port, invokerUrl.getPath(), invokerUrl.getParameters()), invoker);
	}

	/**
	 * 转换为 Invoker， 提供远程的访问
	 */
	@Override
	public <T> Invoker<T> refer(URL url) throws RpcException {
		URL pUrl = new URL(Constants.PROTOCOL, host, port, url.getPath(), url.getParameters());
		return new RemoteInvoker<T>(pUrl);
	}
}