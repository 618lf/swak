package com.swak.config.eventbus;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.google.common.eventbus.Subscribe;
import com.swak.eventbus.EventBus;
import com.swak.utils.Sets;

/**
 * 消费者启动
 * 
 * @author lifeng
 */
public class EventBusPostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {

	private final Set<Object> subscribeBeans = Sets.newHashSet();
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
		return bean;
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
	 * 服务启动后设置引用
	 */
	@Override
	public synchronized void onApplicationEvent(ContextRefreshedEvent arg0) {
		if (eventBus != null) {
			eventBus.init(t -> {
				subscribeBeans.stream().forEach(bean -> {
					eventBus.register(bean);
				});
			});
		}
	}
}