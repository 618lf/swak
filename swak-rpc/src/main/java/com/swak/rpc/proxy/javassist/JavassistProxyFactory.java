package com.swak.rpc.proxy.javassist;

import com.swak.rpc.exception.RpcException;
import com.swak.rpc.invoker.Invocation;
import com.swak.rpc.invoker.Invoker;
import com.swak.rpc.invoker.MethodInvoker;
import com.swak.rpc.proxy.ProxyFactory;

/**
 * 
 * @author lifeng
 */
public class JavassistProxyFactory implements ProxyFactory {

	@Override
	public <T> T getProxy(Invoker<T> invoker) throws RpcException {
		return null;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Invoker<T> getInvoker(Invocation invocation) throws RpcException {
		return new MethodInvoker(invocation);
	}
}