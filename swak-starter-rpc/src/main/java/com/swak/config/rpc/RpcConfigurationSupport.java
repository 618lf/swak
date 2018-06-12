package com.swak.config.rpc;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

import com.swak.rpc.handler.DispatcherRcpHandler;
import com.swak.rpc.handler.RpcFilter;
import com.swak.rpc.handler.RpcHandler;
import com.swak.rpc.invoker.InvokerMapping;
import com.swak.rpc.invoker.ServiceInvokerMapping;
import com.swak.rpc.protocol.Protocol;
import com.swak.rpc.protocol.swak.SwakProtocol;
import com.swak.rpc.proxy.ProxyFactory;
import com.swak.rpc.proxy.javassist.JavassistProxyFactory;
import com.swak.rpc.registry.Registry;
import com.swak.rpc.registry.RegistryProtocol;
import com.swak.rpc.registry.redis.RedisRegistry;
import com.swak.rpc.server.RpcServerProperties;

import io.lettuce.core.RedisClient;

/**
 * web 相关的服务配置
 * @author lifeng
 */
public class RpcConfigurationSupport implements ApplicationContextAware {

	@Nullable
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Nullable
	public final ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
	
	// ---------- registry ---------
	@Bean
	public Registry registry(RedisClient redisClient) {
		return new RedisRegistry(redisClient);
	}
	
	// ---------- protocol ---------
	@Bean
	public Protocol protocol(RpcServerProperties properties) {
		return new SwakProtocol(properties.getHost(), properties.getPort());
	}
	
	// ---------- registry - protocol ---------
	@Bean
	public Protocol registryProtocol(Protocol protocol, Registry rf) {
		return new RegistryProtocol(protocol, rf);
	}
	
	// ---------- proxyFactory ---------
	@Bean
	public ProxyFactory proxyFactory() {
		ProxyFactory proxyFactory = new JavassistProxyFactory();
		return proxyFactory;
	}
	
	// ---------- InvokerMapping ---------
	@Bean
	public InvokerMapping invokerMapping(Protocol registryProtocol, ProxyFactory proxyFactory) {
		InvokerMapping mapping = new ServiceInvokerMapping(registryProtocol, proxyFactory);
		return mapping;
	}
	
	// ---------- rpcHandler ---------
	@Bean
	public RpcHandler rpcHandler() {
		SortedBeanContainer container = new SortedBeanContainer();
		applicationContext.getAutowireCapableBeanFactory().autowireBean(container);
		
		DispatcherRcpHandler rpcHandler = new DispatcherRcpHandler(Collections.unmodifiableList(container.getFilters()),
				Collections.unmodifiableList(container.getInvokerMappings()));
		return rpcHandler;
	}
	
	// Autowire lists for @Bean + @Order
	private static class SortedBeanContainer {
		
		private List<RpcFilter> filters = Collections.emptyList();
		private List<InvokerMapping> invokerMappings = Collections.emptyList();
		
		@Autowired(required = false)
		public void setFilters(List<RpcFilter> filters) {
			this.filters = filters;
		}

		public List<RpcFilter> getFilters() {
			return this.filters;
		}

		public List<InvokerMapping> getInvokerMappings() {
			return invokerMappings;
		}
		@Autowired(required = false)
		public void setInvokerMappings(List<InvokerMapping> invokerMappings) {
			this.invokerMappings = invokerMappings;
		}
	}
}
