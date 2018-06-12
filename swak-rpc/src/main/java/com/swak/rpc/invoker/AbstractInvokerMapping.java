package com.swak.rpc.invoker;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.swak.rpc.annotation.RpcService;

/**
 * 
 * @author lifeng
 */
public abstract class AbstractInvokerMapping implements InvokerMapping {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
		Map<Method, Invocation> methods = MethodIntrospector.selectMethods(userType,
				new MethodIntrospector.MetadataLookup<Invocation>() {
					@Override
					public Invocation inspect(Method method) {
						try {
							return getMappingForMethod(handler, method, userType);
						} catch (Throwable ex) {
							throw new IllegalStateException(
									"Invalid mapping on handler class [" + userType.getName() + "]: " + method, ex);
						}
					}
				}
		);
		
		for (Map.Entry<Method, Invocation> entry : methods.entrySet()) {
			Invocation invocation = entry.getValue();
			if (invocation != null) {
				this.register(invocation);
			}
		}
	}
	
	private Invocation getMappingForMethod(Object handler, Method method, Class<?> handlerType) {
		Invocation info = createRequestMappingInfo(method);
		if (info != null) {
			Invocation typeInfo = createRequestMappingInfo(handlerType);
			if (typeInfo != null) {
				info = typeInfo.combine(info);
			}
		} else {
			info = createRequestMappingInfo(handlerType);
		}
		return info != null? info.fixed(handlerType, handler, method) : null;
	}

	private Invocation createRequestMappingInfo(AnnotatedElement element) {
		RpcService invokerMapping = AnnotatedElementUtils.findMergedAnnotation(element, RpcService.class);
		if (invokerMapping == null) {
			return null;
		}
		return Invocation.build(invokerMapping.version(), invokerMapping.timeout(), invokerMapping.ignore());
	}
	
	/**
	 * 注册
	 * @param mapping
	 * @param userType
	 * @param handler
	 * @param method
	 */
	protected abstract void register(Invocation invocation);
}
