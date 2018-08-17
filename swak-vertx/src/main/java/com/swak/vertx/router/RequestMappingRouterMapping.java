package com.swak.vertx.router;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;

import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.RequestMapping;
import com.swak.vertx.annotation.RequestMethod;
import com.swak.vertx.annotation.ServiceReferer;
import com.swak.vertx.utils.Lifecycle;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;

/**
 * 获取注解，将 controller -> handler
 * 
 * @author lifeng
 */
public class RequestMappingRouterMapping implements ApplicationContextAware {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RequestMappingRouterAdapter handlerAdapter;
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		this.initRequestMappings(applicationContext);
	}

	/**
	 * 自动加载
	 * 
	 * @param applicationContext
	 */
	protected void initRequestMappings(ApplicationContext applicationContext) {
		String[] beanNames = applicationContext.getBeanNamesForAnnotation(Controller.class);
		for (String beanName : beanNames) {
			Object handler = null;
			try {
				handler = applicationContext.getBean(beanName);
			} catch (Throwable ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
				}
			}
			// 校验 handler
			if (handler == null) {
				throw new IllegalStateException("Invalid mapping on handler class [" + beanName + "]");
			}

			// 注册成为处理器
			this.registryMapping(handler);

			// 设置依赖的service服务
			this.serviceProxy(handler);
		}
	}

	/**
	 * 处理方法
	 * 
	 * 注册为 router 的 handler
	 * 
	 * @param handler
	 */
	protected void registryMapping(Object handler) {
		final Class<?> userType = ClassUtils.getUserClass(handler.getClass());
		Map<Method, RouterHandler> methods = MethodIntrospector.selectMethods(userType,
				new MethodIntrospector.MetadataLookup<RouterHandler>() {
					@Override
					public RouterHandler inspect(Method method) {
						try {
							return getRouterHandlerForMethod(handler, method, userType);
						} catch (Throwable ex) {
							throw new IllegalStateException(
									"Invalid mapping on handler class [" + userType.getName() + "]: " + method, ex);
						}
					}
				});

		if (logger.isDebugEnabled()) {
			logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
		}

		for (Map.Entry<Method, RouterHandler> entry : methods.entrySet()) {
			Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), userType);
			RouterHandler mapping = entry.getValue();
			MethodHandler methodHandler = new MethodHandler(handler, invocableMethod);
			mapping.setHandler(methodHandler);
			mapping.setHandlerAdapter(handlerAdapter);
			this.register(mapping, handler, invocableMethod);
		}
	}

	/**
	 * 注册为路由
	 * 
	 * @param mapping
	 * @param handler
	 * @param method
	 */
	protected void register(RouterHandler mapping, Object handler, Method method) {
		Set<String> paths = mapping.getPatterns();
		for (String path : paths) {
			Route route = null;
			if (StringUtils.contains(path, "*")) {
				route = Lifecycle.router.patchWithRegex(path);
			} else {
				route = Lifecycle.router.patch(path);
			}
			if (mapping.getMethod() == RequestMethod.GET) {
				route.method(HttpMethod.GET);
			} else if (mapping.getMethod() == RequestMethod.POST) {
				route.method(HttpMethod.POST);
			}
			route.handler(mapping);
		}
	}

	/**
	 * 转换为 RouterHandler
	 * 
	 * @param handler
	 * @param method
	 * @param handlerType
	 * @return
	 */
	protected RouterHandler getRouterHandlerForMethod(Object handler, Method method, Class<?> handlerType) {
		RouterHandler info = createRouterHandler(method);
		if (info != null) {
			RouterHandler typeInfo = createRouterHandler(handlerType);
			if (typeInfo != null) {
				info = typeInfo.combine(info);
			}
		}
		return info;
	}

	protected RouterHandler createRouterHandler(AnnotatedElement element) {
		RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
		if (requestMapping == null) {
			return null;
		}
		return RouterHandler.paths(requestMapping.method(), requestMapping.value());
	}

	/**
	 * 处理字段
	 * 
	 * @param handler
	 */
	protected void serviceProxy(Object handler) {
		final Class<?> userType = ClassUtils.getUserClass(handler.getClass());
		Field[] fields = userType.getFields();
		for (Field field : fields) {
			this.register(handler, field);
		}
	}

	protected void register(Object handler, Field field) {
		ServiceReferer vertxService = AnnotatedElementUtils.findMergedAnnotation(field, ServiceReferer.class);
		if (vertxService == null) {
			return;
		}
		Class<?> interfaceType = field.getType();
		if (!interfaceType.isInterface()) {
			return;
		}

		try {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}

			Object reference = this.refer(interfaceType);
			if (reference != null) {
				field.set(handler, reference);
			}
		} catch (Exception e) {
			throw new BeanInitializationException("Failed to init remote service reference at filed " + field.getName()
					+ " in class " + handler.getClass().getName(), e);
		}
	}

	protected Object refer(Class<?> interfaceType) {
		Object reference = null;
		try {
			reference = applicationContext.getBean(interfaceType);
		} catch (Exception e) {
		}

		// 已经注册过了
		if (reference != null) {
			return reference;
		}
		
		// 还没有注册
		
	}
}