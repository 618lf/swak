package com.swak.rpc.invoker;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.swak.rpc.annotation.RpcService;
import com.swak.rpc.protocol.RpcRequest;

/**
 * 匹配请求，并执行请求
 * @author lifeng
 */
public class ServiceInvokerMapping implements InvokerMapping{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ConcurrentHashMap<MappingInfo, Invoker<?>> mappingLookup = new ConcurrentHashMap<MappingInfo, Invoker<?>>();

	/**
	 * 初始化 -- 所有注解了 Component
	 */
	public void initInvokerMapping(ApplicationContext applicationContext) {
		  String[] beanNames = applicationContext.getBeanNamesForAnnotation(Component.class);
		  for (String beanName : beanNames) {
				Object handler = null;
				try {
					handler = applicationContext.getBean(beanName);
				} catch (Throwable ex) {
					if (logger.isDebugEnabled()) {
						logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
					}
				}
				this.registryMapping(handler);
			}
		 
	}
	
	/**
	 * 将一个对象注册为mapping
	 * @param handler
	 */
	public void registryMapping(Object handler) {
		final Class<?> userType = ClassUtils.getUserClass(handler.getClass());
		Map<Method, InvokerMappingInfo> methods = MethodIntrospector.selectMethods(userType,
				new MethodIntrospector.MetadataLookup<InvokerMappingInfo>() {
					@Override
					public InvokerMappingInfo inspect(Method method) {
						try {
							return getMappingForMethod(handler, method, userType);
						} catch (Throwable ex) {
							throw new IllegalStateException(
									"Invalid mapping on handler class [" + userType.getName() + "]: " + method, ex);
						}
					}
				}
		);
		
		for (Map.Entry<Method, InvokerMappingInfo> entry : methods.entrySet()) {
			Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), userType);
			InvokerMappingInfo mapping = entry.getValue();
			if (mapping != null) {
				this.register(mapping, userType, handler, invocableMethod);
			}
		}
	}
	
	private InvokerMappingInfo getMappingForMethod(Object handler, Method method, Class<?> handlerType) {
		InvokerMappingInfo info = createRequestMappingInfo(method);
		if (info != null) {
			InvokerMappingInfo typeInfo = createRequestMappingInfo(handlerType);
			if (typeInfo != null) {
				info = typeInfo.combine(info);
			}
		} else {
			info = createRequestMappingInfo(handlerType);
		}
		return info != null? info.fixed(handlerType.getName(), method.getName(), method.getParameterTypes()) : null;
	}

	private InvokerMappingInfo createRequestMappingInfo(AnnotatedElement element) {
		RpcService invokerMapping = AnnotatedElementUtils.findMergedAnnotation(element, RpcService.class);
		if (invokerMapping == null) {
			return null;
		}
		return InvokerMappingInfo.build(invokerMapping.version(), invokerMapping.timeout(), invokerMapping.ignore());
	}
	
	// 注册成为服务 
	protected void register(InvokerMappingInfo mapping, Class<?> classType, Object handler, Method method) {
		mappingLookup.put(mapping, InvokerFactory.build(classType, handler, method));
	}

	/**
	 * 执行请求
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> CompletableFuture<T> invoke(RpcRequest request) {
		MappingInfo info = InvokerMappingInfo.build(request.getVersion())
				.fixed(request.getInterfaceName(), request.getMethodName(), request.getParameterTypes());
		Invoker<?> invoker = mappingLookup.get(info);
		if (invoker == null) {
			throw new InvokeException("no invoker found");
		}
		return (CompletableFuture<T>)invoker.invoke(request.getParameters());
	}
}