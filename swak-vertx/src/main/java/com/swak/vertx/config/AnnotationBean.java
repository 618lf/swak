package com.swak.vertx.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.swak.utils.ConcurrentHashSet;
import com.swak.utils.Lists;
import com.swak.vertx.annotation.RequestMapping;
import com.swak.vertx.annotation.RequestMethod;
import com.swak.vertx.annotation.RestController;
import com.swak.vertx.annotation.ServiceMapping;
import com.swak.vertx.annotation.ServiceReferer;
import com.swak.vertx.utils.RouterUtils;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * 自动化配置bean
 * 
 * @author lifeng
 */
public class AnnotationBean implements BeanPostProcessor, Ordered {

	private final Set<ServiceBean> services = new ConcurrentHashSet<ServiceBean>();
	private final Set<RouterBean> routers = new ConcurrentHashSet<RouterBean>();
	private final ConcurrentMap<String, ReferenceBean> references = new ConcurrentHashMap<String, ReferenceBean>();

	private final Vertx vertx;
	private final Router router;

	public AnnotationBean(Vertx vertx) {
		this.vertx = vertx;
		this.router = Router.router(vertx);
	}
	
	public Vertx getVertx() {
		return vertx;
	}

	public Router getRouter() {
		return router;
	}

	// 需要注册的服务或路由
	public Set<ServiceBean> getServices() {
		return services;
	}
	public Set<RouterBean> getRouters() {
		return routers;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	/**
	 * init reference field
	 *
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = bean.getClass();
		if (AopUtils.isAopProxy(bean)) {
			clazz = AopUtils.getTargetClass(bean);
		}

		// registry router
		RestController controller = clazz.getAnnotation(RestController.class);
		if (controller != null) {
			RequestMapping classMapping = AnnotatedElementUtils.findMergedAnnotation(clazz, RequestMapping.class);
			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
				if (methodMapping == null) {
					continue;
				}

				RouterBean routerBean = this.router(classMapping, methodMapping, beanName, method);
				if (routerBean != null) {
					routers.add(routerBean);
				}
			}
		}

		// fill the reference
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			try {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				ServiceReferer reference = field.getAnnotation(ServiceReferer.class);
				if (reference != null) {
					Object value = refer(reference, field.getType());
					if (value != null) {
						field.set(bean, value);
					}
				}
			} catch (Exception e) {
				throw new BeanInitializationException("Failed to init remote service reference at filed "
						+ field.getName() + " in class " + bean.getClass().getName(), e);
			}
		}
		return bean;
	}

	protected RouterBean router(RequestMapping classMapping, RequestMapping methodMapping, Object bean, Method method) {
		String[] patterns1 = classMapping.value();
		String[] patterns2 = methodMapping.value();
		List<String> result = Lists.newArrayList();
		if (patterns1.length != 0 && patterns2.length != 0) {
			for (String pattern1 : patterns1) {
				for (String pattern2 : patterns2) {
					result.add(RouterUtils.combine(pattern1, pattern2));
				}
			}
		} else if (patterns1.length != 0) {
			result = Lists.newArrayList(patterns1);
		} else if (patterns2.length != 0) {
			result = Lists.newArrayList(patterns2);
		} else {
			result.add("");
		}

		// method
		RequestMethod requestMethod = classMapping.method() == RequestMethod.ALL ? methodMapping.method()
				: classMapping.method();
		requestMethod = requestMethod == RequestMethod.ALL ? null : requestMethod;
		return new RouterBean(bean, method, result, requestMethod);
	}

	protected Object refer(ServiceReferer reference, Class<?> interfaceType) {
		ReferenceBean referenceBean = references.get(interfaceType.getName());
		if (referenceBean == null) {
			referenceBean = new ReferenceBean(interfaceType);
			references.put(interfaceType.getName(), referenceBean);
		}
		return referenceBean.getRefer();
	}

	/**
	 * init service config
	 *
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = bean.getClass();
		if (AopUtils.isAopProxy(bean)) {
			clazz = AopUtils.getTargetClass(bean);
		}
		ServiceMapping serviceMapping = clazz.getAnnotation(ServiceMapping.class);
		if (serviceMapping != null) {
			ServiceBean serviceBean = new ServiceBean(bean);
			services.add(serviceBean);
		}
		return bean;
	}
}