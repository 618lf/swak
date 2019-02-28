package com.swak.reactivex.web.method;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;

import com.swak.reactivex.web.annotation.RequestMapping;

/**
 * 请求处理以及
 * 
 * @author lifeng
 */
public class RequestMappingHandlerMapping extends AbstractRequestMappingHandlerMapping
		implements BeanPostProcessor, Ordered {

	/**
	 * 处理请求
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = bean.getClass();
		if (AopUtils.isAopProxy(bean)) {
			clazz = AopUtils.getTargetClass(bean);
		}
		Controller controller = clazz.getAnnotation(Controller.class);
		if (controller != null) {
			this.registryMapping(bean);
		}
		return bean;
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
