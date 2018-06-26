package com.swak.rpc.remote;

import java.lang.reflect.AnnotatedElement;

import org.springframework.core.annotation.AnnotatedElementUtils;

import com.swak.rpc.annotation.RpcService;
import com.swak.rpc.api.URL;
import com.swak.rpc.invoker.Invocation;
import com.swak.rpc.invoker.Invoker;
import com.swak.rpc.protocol.Protocol;
import com.swak.rpc.proxy.ProxyFactory;

/**
 * 管理远程的服务
 * @author lifeng
 */
public class RemoteInvokerFactory {

	private final Protocol protocol;
	private final ProxyFactory proxyFactory;
	
	/**
	 * 指定协议，来查找服务
	 * @param protocol
	 */
	public RemoteInvokerFactory(Protocol protocol, ProxyFactory proxyFactory) {
		this.protocol = protocol;
		this.proxyFactory = proxyFactory;
	}
	
	/**
	 * 需要注册为服务的接口
	 * 
	 * @param rpc
	 * @return
	 */
	public Object register(Class<?> rpc) {
		Invocation invocation = createRequestMappingInfo(rpc);
		return this.register(invocation);
	}
	
	private Invocation createRequestMappingInfo(AnnotatedElement element) {
		RpcService invokerMapping = AnnotatedElementUtils.findMergedAnnotation(element, RpcService.class);
		if (invokerMapping == null) {
			return null;
		}
		return Invocation.build(invokerMapping.version(), invokerMapping.timeout(), invokerMapping.ignore());
	}
	
	private Object register(Invocation invocation) {
		URL url = new URL(null, null, 0, invocation.getClass().getName(), null);
		Invoker<?> invoker = protocol.refer(url);
		return proxyFactory.getProxy(invoker);
	}
}