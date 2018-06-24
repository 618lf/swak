package com.swak.rpc.proxy.javassist;

import com.swak.asm.Wrapper;
import com.swak.rpc.api.RpcRequest;
import com.swak.rpc.exception.RpcException;
import com.swak.rpc.invoker.AbstractInvoker;
import com.swak.rpc.invoker.Invocation;
import com.swak.rpc.invoker.Invoker;
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
	public <T> Invoker<T> getInvoker(Invocation invocation) throws RpcException {
		Wrapper wrapper = Wrapper.getWrapper(invocation.getServiceType());
		return new AbstractInvoker<T>(invocation) {
			@Override
			@SuppressWarnings("unchecked")
			public T invoke(RpcRequest request) {
				try {
					return (T) wrapper.invokeMethod(invocation.getService(), request.getMethodName(),
							request.getParameterTypes(), request.getParameters());
				} catch (Exception e) {
					throw new RpcException(e);
				}
			}
		};
	}
}