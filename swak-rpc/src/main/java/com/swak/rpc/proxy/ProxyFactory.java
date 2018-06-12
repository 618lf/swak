package com.swak.rpc.proxy;

import com.swak.rpc.exception.RpcException;
import com.swak.rpc.invoker.Invocation;
import com.swak.rpc.invoker.Invoker;

/**
 * 1. 作为客户端使用，则封装了底层的网络访问的细节
 * 2. 作为服务端使用，则封装了具体的方法调用的细节
 * 
 * @author lifeng
 */
public interface ProxyFactory {

	/**
	 * 客户端使用
	 * @param invoker
	 * @return
	 * @throws RpcException
	 */
	<T> T getProxy(Invoker<T> invoker) throws RpcException;
	
	/**
	 * 服务端使用
	 * @param proxy
	 * @param type
	 * @param url
	 * @return
	 * @throws RpcException
	 */
	<T> Invoker<T> getInvoker(Invocation invocation) throws RpcException;
}