package com.swak.rpc.invoker;

import com.swak.rpc.api.RpcRequest;
import com.swak.rpc.api.URL;
import com.swak.rpc.client.ConnectorContext;

/**
 * 提供远程访问
 * @author lifeng
 */
public class RemoteInvoker<T> implements Invoker<T> {

	private URL url;
	private ConnectorContext context;
	
	public RemoteInvoker(URL url) {
		this.url = url;
	}
	
	@Override
	public URL getURL() {
		return url;
	}

	@Override
	public T invoke(RpcRequest request) {
		context.sent(request);
		return null;
	}
}