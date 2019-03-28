package com.swak.config.mq;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.swak.rabbit.EventBus;
import com.swak.rabbit.annotation.Subscribe;
import com.swak.utils.ConcurrentHashSet;

/**
 * 消费者启动
 * 
 * @author lifeng
 */
public class RabbitMqPostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {

	private final ConcurrentHashSet<Object> referenceBeans = new ConcurrentHashSet<>();
	private EventBus eventBus;

	/**
	 * 保留EventBus对象
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = bean.getClass();
		if (clazz.isAssignableFrom(EventBus.class)) {
			eventBus = (EventBus) bean;
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
					referenceBeans.add(bean);
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
				referenceBeans.stream().forEach(bean -> {
					eventBus.register(bean);
				});
			});
		}
	}
}