package com.swak.reactivex.web.method;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;

import com.swak.reactivex.web.annotation.RequestMapping;

/**
 * 请求处理以及
 * 
 * @author lifeng
 */	
public class RequestMappingHandlerMapping extends AbstractRequestMappingHandlerMapping implements Ordered {

	/**
	 * 自动加载
	 * @param applicationContext
	 */
	@Override
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
		    	throw new IllegalStateException(
						"Invalid mapping on handler class [" + beanName  + "]");
		    }
			this.registryMapping(handler);
		}
	}
	
	@Override
	protected RequestMappingInfo getMappingForMethod(Object handler, Method method, Class<?> handlerType) {
		RequestMappingInfo info = createRequestMappingInfo(method);
		if (info != null) {
			RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
			if (typeInfo != null) {
				info = typeInfo.combine(info);
			}
		}
		return info;
	}

	protected RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
		RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
		if (requestMapping == null) {
			return null;
		}
		return RequestMappingInfo.paths(requestMapping.method(), requestMapping.value());
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
