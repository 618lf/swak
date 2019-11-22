package com.swak.config.mq;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

import com.swak.booter.AppBooter;
import com.swak.rabbit.EventBus;
import com.swak.rabbit.annotation.Publisher;
import com.swak.rabbit.annotation.Subscribe;
import com.swak.rabbit.handler.ReferenceBean;
import com.swak.utils.Maps;
import com.swak.utils.Sets;

/**
 * 消费者启动
 * 
 * @author lifeng
 */
public class RabbitMqPostProcessor extends AppBooter implements BeanPostProcessor {

	private final Set<Object> subscribeBeans = Sets.newHashSet();
	private final Map<ReferenceBean, Object> publisherBeans = Maps.newHashMap();
	private EventBus eventBus;

	/**
	 * 保留EventBus对象
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = bean.getClass();
		if (clazz.isAssignableFrom(EventBus.class)) {
			eventBus = (EventBus) bean;
			return bean;
		}

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
				Publisher reference = field.getAnnotation(Publisher.class);
				if (reference != null) {
					Object value = refer(reference, field.getType());
					if (value != null) {
						field.set(bean, value);
					}
				}
			} catch (Exception e) {
				throw new BeanInitializationException("Failed to init Publisher reference at filed " + field.getName()
						+ " in class " + bean.getClass().getName(), e);
			}
		}
		return bean;
	}

	protected Object refer(Publisher reference, Class<?> interfaceType) {
		ReferenceBean referenceBean = new ReferenceBean(reference, interfaceType);
		if (!publisherBeans.containsKey(referenceBean)) {
			publisherBeans.put(referenceBean, referenceBean.newRefer());
		}
		return publisherBeans.get(referenceBean);
	}

	/**
	 * 初始化结束之后注册
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = bean.getClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
				Subscribe reference = method.getAnnotation(Subscribe.class);
				if (reference != null) {
					subscribeBeans.add(bean);
				}
			}
		}
		return bean;
	}

	/**
	 * 系统启动之后再执行 EventBus
	 */
	protected synchronized void onApplicationEvent(ApplicationContext context) {
		if (eventBus != null) {
			eventBus.init(t -> {
				subscribeBeans.stream().forEach(bean -> {
					eventBus.register(bean);
				});
			});
		}
	}
}