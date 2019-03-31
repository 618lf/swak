package com.swak.flux.config;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

import com.swak.flux.verticle.Flux;
import com.swak.flux.verticle.FluxImpl.DeploymentOptions;
import com.swak.flux.verticle.InvokerHandler;
import com.swak.flux.verticle.ServiceVerticle;
import com.swak.flux.verticle.annotation.FluxReferer;
import com.swak.flux.verticle.annotation.FluxService;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

/**
 * 收集整个前台或后台的服务
 * 
 * @author lifeng
 */
public class AnnotationBean implements BeanPostProcessor, Ordered {
	private final Map<String, Object> references = Maps.newOrderMap();
	private Flux flux;

	public AnnotationBean(Flux flux) {
		this.flux = flux;
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

		// fill the reference
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			try {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				FluxReferer reference = field.getAnnotation(FluxReferer.class);
				if (reference != null) {
					Object value = refer(reference, field.getType());
					if (value != null) {
						field.set(bean, value);
					}
				}
			} catch (Exception e) {
				throw new BeanInitializationException("Failed to init service reference at filed " + field.getName()
						+ " in class " + bean.getClass().getName(), e);
			}
		}
		return bean;
	}

	protected Object refer(FluxReferer reference, Class<?> interfaceType) {
		Object referenceBean = references.get(interfaceType.getName());
		if (referenceBean == null) {
			references.put(interfaceType.getName(), newRefer(interfaceType));
		}
		return referenceBean;
	}

	private Object newRefer(Class<?> interfaceType) {
		return Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[] { interfaceType },
				new InvokerHandler(flux, interfaceType));
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
		FluxService serviceMapping = clazz.getAnnotation(FluxService.class);
		if (serviceMapping != null) {
			Class<?>[] classes = ClassUtils.getAllInterfacesForClass(clazz);
			if (classes == null || classes.length == 0) {
				throw new BeanInitializationException("Failed to init service " + beanName + " in class "
						+ bean.getClass().getName() + ", that need realize one interface");
			}
			for (Class<?> inter : classes) {
				deploymentService(serviceMapping, inter, bean);
			}
		}
		return bean;
	}

	// 发布服务
	private void deploymentService(FluxService serviceMapping, Class<?> inter, Object bean) {
		DeploymentOptions options = null;
		if (StringUtils.isNotBlank(serviceMapping.use_pool())) {
			options = new DeploymentOptions();
			options.setWorkPoolName(serviceMapping.use_pool());
		}
		ServiceVerticle verticle = new ServiceVerticle(bean, inter);
		flux.deployment(verticle, options);
	}

	@Override
	public int getOrder() {
		return 0;
	}
}