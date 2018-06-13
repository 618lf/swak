package com.swak.rpc.invoker;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.swak.rpc.api.RpcRequest;
import com.swak.rpc.protocol.Exporter;
import com.swak.rpc.protocol.Protocol;
import com.swak.rpc.proxy.ProxyFactory;

/**
 * 匹配请求，并执行请求
 * @author lifeng
 */
public class ServiceInvokerMapping extends AbstractInvokerMapping implements ApplicationContextAware {
	
	private final Protocol protocol;
	private final ProxyFactory proxyFactory;
	private ConcurrentHashMap<String, Exporter<?>> mappingLookup = new ConcurrentHashMap<String, Exporter<?>>();
	
	/**
	 * 指定协议，来查找服务
	 * @param protocol
	 */
	public ServiceInvokerMapping(Protocol protocol, ProxyFactory proxyFactory) {
		this.protocol = protocol;
		this.proxyFactory = proxyFactory;
	}

	/**
	 * 初始化配置项
	 * @param applicationContext
	 * @throws BeansException
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.initInvokerMapping(applicationContext);
	}
	
	/**
	 * 注册成为服务
	 */
	@Override
	protected void register(Invocation invocation) {
		
		/**
		 * 构建执行器， 基于方法的
		 */
		Invoker<?> invoker = proxyFactory.getInvoker(invocation);
		
		/**
		 * 暴露服务
		 */
		Exporter<?> exporter = protocol.export(invoker);
		
		/**
		 * 存储
		 */
		mappingLookup.put(exporter.getURL().getSequence(), exporter);
	}

	/**
	 * 查找请求
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> Invoker<CompletableFuture<T>> lookup(RpcRequest request) {
		
		// 获取暴露的服务
		Exporter<?> exporter = mappingLookup.get(request.getSequence());
		
		// 返回实际的调用 
		return exporter == null ? null: (Invoker<CompletableFuture<T>>) exporter.getInvoker();
	}
}