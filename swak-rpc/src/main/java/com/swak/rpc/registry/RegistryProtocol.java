package com.swak.rpc.registry;

import java.util.List;

import com.swak.rpc.api.URL;
import com.swak.rpc.exception.RpcException;
import com.swak.rpc.invoker.Invoker;
import com.swak.rpc.protocol.Exporter;
import com.swak.rpc.protocol.Protocol;

/**
 * facade for Protocol and Protocol
 * @author lifeng
 */
public class RegistryProtocol implements Protocol {
	
	private Protocol protocol;
	private Registry registry;
	
	public RegistryProtocol(Protocol protocol, Registry registry) {
		this.protocol = protocol;
		this.registry = registry;
	}
	
	@Override
	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		Exporter<T> exporter = protocol.export(invoker);
		registry.register(exporter.getURL());
		return exporter;
	}

	@Override
	public <T> Invoker<T> refer(URL url) throws RpcException {
		List<URL> urls = registry.lookup(url);
		return null;
		//return protocol.refer(type, url);
	}
}
