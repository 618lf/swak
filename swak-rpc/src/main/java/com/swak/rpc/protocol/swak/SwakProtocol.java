package com.swak.rpc.protocol.swak;

import com.swak.rpc.api.Constants;
import com.swak.rpc.api.URL;
import com.swak.rpc.exception.RpcException;
import com.swak.rpc.invoker.Invoker;
import com.swak.rpc.protocol.DefaultExporter;
import com.swak.rpc.protocol.Exporter;
import com.swak.rpc.protocol.Protocol;

/**
 * 简单的二进制协议
 * @author lifeng
 */
public class SwakProtocol implements Protocol {
	
	private String host;
	private int port;
	
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

	@Override
	public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
		return null;
	}
}