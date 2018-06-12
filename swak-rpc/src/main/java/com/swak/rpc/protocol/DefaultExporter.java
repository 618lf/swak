package com.swak.rpc.protocol;

import com.swak.rpc.api.URL;
import com.swak.rpc.invoker.Invoker;

public class DefaultExporter<T> implements Exporter<T> {
	
	final URL url;
	final Invoker<T> invoker;
	
	public DefaultExporter(URL url, Invoker<T> invoker) {
		this.url = url;
		this.invoker = invoker;
	}

	@Override
	public URL getURL() {
		return url;
	}

	@Override
	public Invoker<T> getInvoker() {
		return invoker;
	}
}
