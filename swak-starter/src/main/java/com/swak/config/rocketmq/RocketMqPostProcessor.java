package com.swak.config.rocketmq;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.swak.rocketmq.EventBus;
import com.swak.rocketmq.annotation.Listener;
import com.swak.rocketmq.annotation.Subscribe;
import com.swak.utils.ConcurrentHashSet;

/**
 * 消费者，生产者启动
 * 
 * @author lifeng
 */
public class RocketMqPostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {

	private final ConcurrentHashSet<Object> subscribes = new ConcurrentHashSet<>();
	private final ConcurrentHashSet<Object> listeners = new ConcurrentHashSet<>();
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
					subscribes.add(bean);
				}
				Listener listener = method.getAnnotation(Listener.class);
				if (listener != null) {
					listeners.add(bean);
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
				subscribes.stream().forEach(bean -> {
					eventBus.register(bean);
				});
				listeners.stream().forEach(bean -> {
					eventBus.listener(bean);
				});
			});
		}
	}
}